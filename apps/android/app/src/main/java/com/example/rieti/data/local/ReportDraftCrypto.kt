
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
 * Utiliza AES-GCM y Android Keystore.
 *
 * La clave de cifrado permanece administrada
 * por Android Keystore.
 */
object ReportDraftCrypto {

    private const val KEY_ALIAS = "rieti_report_draft_key"

    private const val TRANSFORMATION =
        "AES/GCM/NoPadding"

    private const val IV_LENGTH = 12

    private const val TAG_LENGTH_BITS = 128

    /**
     * Obtiene la clave existente o genera una nueva.
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
     * Se almacena el IV junto al texto cifrado
     * para poder recuperarlo posteriormente.
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
     * Si el contenido fue alterado o la clave
     * ya no está disponible, se produce un error.
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
