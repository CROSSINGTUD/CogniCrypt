/********************************************************************************
 * Copyright (c) 2015-2021 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.encryptionstrings;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

/**
 * The Class SecureEncryptor for encryption of a string.
 */
public class SecureEncryptor {

	/**
	 * Gets a password to generate a key together with a random salt and
	 * hashes the key to create a secure secret key for later symmetric encryption or decryption.
	 *
	 * @param pwd the user's chosen password for a password-based encryption (PBE).
	 * @return encryptionKey the secret key to be used for later encryption.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchAlgorithmException This exception is thrown if no Provider supports a SecretKeyFactorySpi or SecureRadnomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 */
	public javax.crypto.SecretKey generateKey(char[] pwd) {
		byte[] salt = new byte[32];
		javax.crypto.SecretKey encryptionKey = null;
		int keysize = 128;
		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(salt, "next").includeClass("javax.crypto.spec.PBEKeySpec")
			.addParameter(pwd, "password").addParameter(keysize, "keylength").includeClass("javax.crypto.SecretKeyFactory").includeClass("javax.crypto.SecretKey")
			.includeClass("javax.crypto.spec.SecretKeySpec").addParameter(encryptionKey, "this").generate();

		return encryptionKey;
	}
	
	/**
	 * First converts the plaintext to bytes and then encrypts it with cipher using the input secret key and algorithm 
	 * specifications provided by initialized vector parameter (IvParameterSpec) from random bytes of same size as key.
	 * Copies the ivBytes and the result of the encryption in a new byte array and encode it with Base64 to string.
	 * AES algorithm with a block size of 128 bits has been used to encrypt the data.
	 * 
	 * @param plaintext the input string to be encrypted.
	 * @param key the secret key for encryption, it also will be used for decryption.
	 * @param plain_off the offset in input text where the input starts. 0, if all bytes in plaintext need to be encrypted.
	 * @param len the length of the plaintext.
	 * @return a string that contains the ivBytes and the outcome of encryption.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown if the character encoding is not supported, while converting string to bytes and vice versa.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingException This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.lang.String encrypt(java.lang.String plaintext, javax.crypto.SecretKey key) throws IOException {
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] res = null;
		byte[] plaintextString = plaintext.getBytes(StandardCharsets.UTF_8);
		int mode = Cipher.ENCRYPT_MODE;

		CrySLCodeGenerator.getInstance().includeClass("java.security.SecureRandom").addParameter(ivBytes, "next").includeClass("javax.crypto.spec.IvParameterSpec")
			.addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher").addParameter(mode, "encmode").addParameter(plaintextString, "plainText").addParameter(key, "key")
			.addParameter(res, "cipherText").generate();

		byte[] ret = new byte[ivBytes.length + res.length];
		System.arraycopy(ivBytes, 0, ret, 0, ivBytes.length);
		System.arraycopy(res, 0, ret, ivBytes.length, res.length);

		return Base64.getEncoder().encodeToString(ret);
	}

	/**
	 * Converts the ciphertext string to array bytes, then divides it into two parts, ivBytes and data. Then decrypts
	 * the data with the input secret key that was used to encrypt the data. Stores the result in a byte array,
	 * converts it to string and returns the string. The same algorithm from encryption has been used to decrypt
	 * , the AES algorithm with 128 bits block size.
	 *
	 * @param ciphertext The encrypted string to be decrypted. Includes ivBytes as first part and the encrypted data as the second part.
	 * @param key the secret key that was used for encryption.
	 * @param plain_off the offset in input ciphertext where the input starts. 0, if all bytes in ciphertext need to be decrypted.
	 * @param len the length of the ciphertext.
	 * @return the decrypted string.
	 * @throws InvalidAlgorithmParameterException This exception is thrown when the given algorithm parameters are inappropriate for the cipher.
	 * @throws GeneralSecurityException This exception is thrown if a security-related exception happens that extends this general exception.
	 * @throws NoSuchPaddingException This exception is thrown when the chosen padding is not supported in this environment.
	 * @throws IllegalBlockSizeException This exception is thrown when the size of input data is not a multiple of the block-size or if the encryption algorithm is unable to process the input data provided.
	 * @throws ShortBufferException This exception is thrown when an output buffer provided by the user is too short to hold the operation result.
	 * @throws IOException This exception is thrown if the character encoding is not supported, while converting string to bytes and vice versa.
	 * @throws NoSuchAlgorithmException This exception is thrown if no provider supports a CipherSpi or SecureRandomSpi implementation for the specified algorithms.
	 * @throws InvalidKeySpecException This exception is thrown when key specifications are invalid.
	 * @throws BadPaddingExceptionxception This exception is thrown when padding is wrong or not compatible with cipher block size and data size.
	 * @throws InvalidKeyException This exception is thrown in case of invalid Keys (invalid encoding, wrong length, uninitialized, etc).
	 */
	public java.lang.String decrypt(java.lang.String ciphertext, javax.crypto.SecretKey key) throws IOException {
		byte[] ciphertextString = Base64.getDecoder().decode(ciphertext);
		byte[] ivBytes = new byte[key.getEncoded().length];
		byte[] data = new byte[ciphertextString.length - ivBytes.length];
		System.arraycopy(ciphertextString, 0, ivBytes, 0, ivBytes.length);
		System.arraycopy(ciphertextString, ivBytes.length, data, 0, data.length);

		int mode = Cipher.DECRYPT_MODE;
		byte[] res = null;
		CrySLCodeGenerator.getInstance().includeClass("javax.crypto.spec.IvParameterSpec").addParameter(ivBytes, "iv").includeClass("javax.crypto.Cipher")
			.addParameter(mode, "encmode").addParameter(data, "plainText").addParameter(key, "key").addParameter(res, "cipherText").generate();

		return new String(res, StandardCharsets.UTF_8);
	}

}
