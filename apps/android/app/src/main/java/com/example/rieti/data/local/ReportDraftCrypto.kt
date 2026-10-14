
package com.example.rieti.data.local

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Cifra y descifra el contenido sensible
 * de los borradores de RIETI.
 *
 * Utiliza el algoritmo AES con claves de 256 bits
 * en modo GCM (Galois/Counter Mode), que proporciona
 * confidencialidad y autenticación del contenido cifrado.
 *
 * La clave de cifrado se genera y administra mediante
 * Android Keystore, evitando almacenarla directamente
 * dentro del código o de la base de datos local.
 *
 * Para permitir el descifrado posterior, el vector
 * de inicialización (IV) se almacena junto con
 * el contenido cifrado en una representación Base64.
 *
 * @author Equipo 3:
 * Lorena Antonio Monraga,
 * Eduardo Márquez López,
 * Ángel Adrik Merino Cedeño,
 * Marco Polo Peña Rivera,
 * Said Valseca Figueroa.
 */
object ReportDraftCrypto {

    /**
     * Alias utilizado para identificar y recuperar
     * la clave de cifrado dentro de Android Keystore.
     */
    private const val KEY_ALIAS = "rieti_report_draft_key"

    /**
     * Transformación criptográfica utilizada para
     * cifrar y descifrar la información.
     *
     * AES/GCM/NoPadding indica el uso de AES en modo GCM,
     * sin un esquema adicional de relleno.
     */
    private const val TRANSFORMATION =
        "AES/GCM/NoPadding"

    /**
     * Longitud esperada del vector de inicialización
     * en bytes para recuperar el contenido cifrado.
     *
     * Se utiliza un IV de 12 bytes (96 bits).
     */
    private const val IV_LENGTH = 12

    /**
     * Longitud de la etiqueta de autenticación
     * utilizada por AES-GCM durante el descifrado.
     *
     * Se configura una etiqueta de 128 bits.
     */
    private const val TAG_LENGTH_BITS = 128

    /**
     * Obtiene la clave existente o genera una nueva.
     */

    /**
     * Obtiene la clave existente o genera una nueva.
     *
     * Accede al proveedor AndroidKeyStore y comprueba
     * si ya existe una clave asociada con KEY_ALIAS.
     *
     * Si la clave está disponible, la reutiliza.
     * En caso contrario, genera una clave AES
     * de 256 bits configurada para operaciones
     * de cifrado y descifrado en modo GCM.
     *
     * La clave permanece administrada por
     * Android Keystore.
     *
     * @return Clave secreta AES existente o
     * generada para proteger los borradores.
     *
     * @throws java.security.GeneralSecurityException
     * Si ocurre un error al acceder al almacén
     * de claves o durante su generación.
     */
    private fun getOrCreateKey(): SecretKey {

        val keyStore = KeyStore.getInstance(
            "AndroidKeyStore"
        ).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(
            KEY_ALIAS,
            null
        ) as? SecretKey

        if (existingKey != null) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or
                    KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(
                KeyProperties.BLOCK_MODE_GCM
            )
            .setEncryptionPaddings(
                KeyProperties.ENCRYPTION_PADDING_NONE
            )
            .setKeySize(256)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }


    /**
     * Cifra un texto y devuelve su representación Base64.
     *
     * Obtiene la clave AES mediante getOrCreateKey()
     * e inicializa un objeto Cipher en modo de cifrado.
     *
     * Convierte el texto original a bytes UTF-8
     * y aplica la transformación AES/GCM/NoPadding.
     *
     * Se almacena el IV junto al texto cifrado
     * para poder recuperarlo posteriormente.
     *
     * Finalmente, convierte los bytes combinados
     * a una cadena Base64 sin saltos de línea.
     *
     * @param plainText Texto original que se desea cifrar.
     *
     * @return Cadena Base64 que contiene el IV
     * seguido del contenido cifrado y su etiqueta
     * de autenticación GCM.
     *
     * @throws java.security.GeneralSecurityException
     * Si ocurre un error al inicializar el cifrado,
     * recuperar la clave o procesar el contenido.
    */
    fun encrypt(plainText: String): String {

        val cipher = Cipher.getInstance(
            TRANSFORMATION
        )

        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateKey()
        )

        val iv = cipher.iv

        val encryptedBytes = cipher.doFinal(
            plainText.toByteArray(
                StandardCharsets.UTF_8
            )
        )

        val combined = iv + encryptedBytes

        return Base64.encodeToString(
            combined,
            Base64.NO_WRAP
        )
    }

    /**
     * Descifra el contenido previamente cifrado.
     *
     * Decodifica la cadena Base64 y separa
     * el vector de inicialización de los bytes cifrados.
     *
     * Posteriormente, recupera la clave AES desde
     * Android Keystore e inicializa Cipher
     * en modo de descifrado.
     *
     * Utiliza GCMParameterSpec para establecer
     * la longitud de la etiqueta de autenticación
     * y el IV empleado durante el cifrado.
     *
     * Si el contenido fue alterado o la clave
     * ya no está disponible, se produce un error.
     *
     * @param encryptedText Cadena Base64 que contiene
     * el IV y el contenido cifrado.
     *
     * @return Texto original recuperado en formato UTF-8.
     *
     * @throws IllegalArgumentException Si el contenido
     * decodificado no supera la longitud del IV.
     * @throws java.security.GeneralSecurityException
     * Si falla la recuperación de la clave,
     * la autenticación GCM o el descifrado.
     */
    fun decrypt(encryptedText: String): String {

        val combined = Base64.decode(
            encryptedText,
            Base64.NO_WRAP
        )

        require(combined.size > IV_LENGTH) {
            "El contenido cifrado no es válido."
        }

        val iv = combined.copyOfRange(
            0,
            IV_LENGTH
        )

        val encryptedBytes = combined.copyOfRange(
            IV_LENGTH,
            combined.size
        )

        val cipher = Cipher.getInstance(
            TRANSFORMATION
        )

        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateKey(),
            GCMParameterSpec(
                TAG_LENGTH_BITS,
                iv
            )
        )

        val decryptedBytes = cipher.doFinal(
            encryptedBytes
        )

        return String(
            decryptedBytes,
            StandardCharsets.UTF_8
        )
    }
}
