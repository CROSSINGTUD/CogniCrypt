/********************************************************************************
 * Copyright (c) 2015-2019 TU Darmstadt, Paderborn University
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 ********************************************************************************/

package de.cognicrypt.codegenerator.crysl.templates.digitalsignatures;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import de.cognicrypt.codegenerator.crysl.CrySLCodeGenerator;

public class Signer {

	public static java.security.KeyPair getKey() throws NoSuchAlgorithmException {
		java.security.KeyPair pair = null;
		CrySLCodeGenerator.getInstance().includeClass("java.security.KeyPairGenerator").addReturnObject(pair).generate();
		return pair;
	}

	public static byte[] sign(String msg, java.security.KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
		byte[] res = null;
		java.security.PrivateKey privKey = keyPair.getPrivate();
		CrySLCodeGenerator.getInstance().includeClass("java.security.Signature").addParameter(privKey, "priv").addParameter(msgBytes, "inpba").addReturnObject(res).generate();
		return res;
	}

	public static boolean vfy(String msg, java.security.KeyPair keyPair) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		boolean res = false;
		byte[] msgBytes = msg.getBytes(StandardCharsets.UTF_8);
		java.security.PublicKey pubKey = keyPair.getPublic();
		CrySLCodeGenerator.getInstance().includeClass("java.security.Signature").addParameter(pubKey, "pub").addParameter(msgBytes, "sign").addReturnObject(res).generate();
		return res;
	}

}
