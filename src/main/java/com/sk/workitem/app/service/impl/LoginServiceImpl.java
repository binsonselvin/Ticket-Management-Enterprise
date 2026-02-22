package com.sk.workitem.app.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.sk.workitem.app.config.paths.PathLocator;
import com.sk.workitem.app.constants.SystemUserConstant;
import com.sk.workitem.app.model.MasterLogin;
import com.sk.workitem.app.model.MasterOtp;
import com.sk.workitem.app.repository.MasterLoginRepository;
import com.sk.workitem.app.repository.MasterOrganizationRepository;
import com.sk.workitem.app.repository.MasterOtpRepository;
import com.sk.workitem.app.service.LoginService;

import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;

@Component
public class LoginServiceImpl implements LoginService {

	Logger log = LogManager.getLogger(getClass());

	private MasterOrganizationRepository masterOrgRepo;
	private MasterLoginRepository masterLoginRepo;
	private MasterOtpRepository otpMaster;

	public LoginServiceImpl(MasterOrganizationRepository masterOrgRepo, MasterLoginRepository masterLoginRepo,
			MasterOtpRepository otpMaster) {
		this.masterOrgRepo = masterOrgRepo;
		this.masterLoginRepo = masterLoginRepo;
		this.otpMaster = otpMaster;
	}

	/***
	 * Reads data in bytes from the certificate
	 * 
	 * @param filename @{link String} file with absolute path
	 * @return byte[] of the file content
	 * @throws IOException
	 */
	public byte[] readFileBytes(String filename) throws IOException {
		Path path = Paths.get(filename);
		return Files.readAllBytes(path);
	}

	/***
	 * Reads file content from the specified file/certificate
	 * 
	 * @param filename {@link String} location of public key
	 * @return {@link PublicKey}
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PublicKey readPublicKey(String filename)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(readFileBytes(filename));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicSpec);
	}

	/***
	 * Reads file content from the specified file/certificate
	 * 
	 * @param filename {@link String} location of public key
	 * @return {@link PrivateKey}
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PrivateKey readPrivateKey(String filename)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(readFileBytes(filename));
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	/***
	 * Encrypts data using public key
	 * 
	 * @param key       {@link PublicKey}
	 * @param plaintext byte[] data in plain text
	 * @return byte[] encrypted data in byte array
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encrypt(PublicKey key, byte[] plaintext) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		return cipher.doFinal(plaintext);
	}

	/***
	 * 
	 * @param key        {@link PrivateKey}
	 * @param ciphertext byte[] ecrypted data
	 * @return byte[] decrypted data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] decrypt(PrivateKey key, byte[] ciphertext) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(ciphertext);
	}

	@Override
	public byte[] encryptPasswordPublicKey(String password) {

		try {
			PublicKey publicKey = readPublicKey(PathLocator.PUBLICKEY);
			byte[] message = password.getBytes();
			return encrypt(publicKey, message);
		} catch (Exception e) {
			log.error("Password Encryption Failed: ", e);
		}

		return null;
	}

	@Override
	public byte[] decryptPasswordPrivateKey(byte[] password) {

		try {
			PrivateKey privateKey = readPrivateKey(PathLocator.PRIVATEKEY);
			return decrypt(privateKey, password);
		} catch (Exception e) {
			log.error("Password Decryption Failed: ", e);
		}
		return null;
	}

	@Override
	public String hashPassword(String password, String salt) {
		return BCrypt.hashpw(password, salt);
	}

	@Override
	public String genPasswordSalt() {
		return BCrypt.gensalt(10);
	}

	@Override
	public boolean checkOrgExist(String email) {
		return masterOrgRepo.findByUserEmail(email) == null ? false : true;
	}

	@Override
	public MasterLogin checkUserExists(String email) {

		Optional<MasterLogin> usrMstrObj = masterLoginRepo.findById(email);
		if (!usrMstrObj.isEmpty()) {
			log.info("User Exists in Database: {}", usrMstrObj.get().getUserEmail());
			return usrMstrObj.get();
		} else {
			return null;
		}
	}

	@Override
	public boolean validateUserPwd(String formPwd, String dbHash) {
		return BCrypt.checkpw(formPwd, dbHash);
	}

	@Override
	public String generateSecurityCode() {
		SecureRandom secureRandom = new SecureRandom();
		int security = secureRandom.nextInt(100000, 999999);
		return security + "";
	}

	@Override
	public void updateLoginTimestamps(MasterLogin mstrLogin) {
		LocalDateTime currTime = LocalDateTime.now();
		if (Objects.nonNull(mstrLogin)) {
			mstrLogin.setLastSuccLogin(currTime);
			mstrLogin.setLastValidOtp(currTime);
			mstrLogin.setFailedCount(0);
			mstrLogin.setInvalidOtpCount(0);
			mstrLogin.setResendCount(0);
			mstrLogin.setModifiedAt(currTime);
			mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
			masterLoginRepo.save(mstrLogin);
		}
	}

	@Override
	public void updateFailureLoginTimestamps(MasterLogin mstrLogin) {
		if (Objects.nonNull(mstrLogin)) {
			LocalDateTime currTime = LocalDateTime.now();

			mstrLogin.setLastUnSuccLogin(currTime);
			mstrLogin.setFailedCount(mstrLogin.getFailedCount() + 1);
			mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
			mstrLogin.setModifiedAt(currTime);
			if (mstrLogin.getFailedCount() >= 3) {
				mstrLogin.setFailedCount(3);
				mstrLogin.setAccLocked(true);
			}
			masterLoginRepo.save(mstrLogin);
		}
	}

	@Override
	public boolean updateAccountPassword(String email, String password) {

		Optional<MasterLogin> mstrLoginOpt = masterLoginRepo.findById(email);
		if (!masterLoginRepo.findById(email).isEmpty()) {
			try {
				MasterLogin mstrLogin = mstrLoginOpt.get();
				String salt = genPasswordSalt();
				byte[] passEnc = encryptPasswordPublicKey(password);

				mstrLogin.setAccLocked(false);
				mstrLogin.setFailedCount(0);
				mstrLogin.setInvalidOtpCount(0);
				mstrLogin.setModifiedAt(LocalDateTime.now());
				mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
				mstrLogin.setUserEmail(email);
				mstrLogin.setSalt(genPasswordSalt());
				mstrLogin.setPasswordHash(hashPassword(password, salt));
				mstrLogin.setPassEncrypted(passEnc);
				masterLoginRepo.save(mstrLogin);

				return true;
			} catch (Exception e) {
				log.error(e);
				return false;
			}
		}
		return false;
	}

	@Override
	public MasterOtp getLastLoginOtp(String otpGenFor, String email) {
		Sort sort = Sort.by(Direction.DESC, "otpExpireTime");
		return otpMaster.findFirstByUserEmailAndOtpExpiredAndOtpGenFor(email, false, otpGenFor, sort);
	}

	@Override
	public boolean checkResendCountExceeded(String email) {
		Optional<MasterLogin> loginObj = masterLoginRepo.findById(email);
		if (!loginObj.isEmpty()) {
			if (loginObj.get().getResendCount() > 3) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean increaseResendCount(String email) {
		Optional<MasterLogin> loginObj = masterLoginRepo.findById(email);
		if (!loginObj.isEmpty()) {
			MasterLogin mstrLogin = loginObj.get();
			int loginCount = mstrLogin.getResendCount();
			loginCount = loginCount + 1;
			if(loginCount > 3) {
				loginCount = 4;
				mstrLogin.setAccLocked(true);
			}
			mstrLogin.setResendCount(loginCount);
			mstrLogin.setModifiedAt(LocalDateTime.now());
			mstrLogin.setModifiedBy(SystemUserConstant.SYSTEM);
			masterLoginRepo.save(mstrLogin);
		}
		return true;
	}
}
