package com.ll.TeamProject.domain.user.service

import com.ll.TeamProject.domain.user.entity.SiteUser
import com.ll.TeamProject.domain.user.exceptions.UserErrorCode
import com.ll.TeamProject.domain.user.repository.UserRepository
import com.ll.TeamProject.global.exceptions.CustomException
import com.ll.TeamProject.global.mail.GoogleMailService
import com.ll.TeamProject.global.redis.RedisService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class AccountVerificationService(
    private val userRepository: UserRepository,
    private val emailService: GoogleMailService,
    private val passwordEncoder: PasswordEncoder,
    private val redisService: RedisService
) {
    companion object {
        private const val VERIFICATION_CODE_KEY = "verificationCode:"
        private const val PASSWORD_RESET_KEY = "password-reset:"
        private const val VERIFICATION_CODE_EXPIRATION = 180
        private const val PASSWORD_RESET_EXPIRATION = 300
    }

    fun processVerification(username: String, email: String) {
        val user = validateUsernameAndEmail(username, email)

        val code = generateVerificationCode()
        val redisKey = getKey(VERIFICATION_CODE_KEY, username)

        redisService.setValue(redisKey, code, VERIFICATION_CODE_EXPIRATION.toLong())
        sendVerificationEmail(user.nickname, user.email, code)
    }

    private fun validateUsernameAndEmail(username: String, email: String): SiteUser {
        return userRepository.findByUsername(username)
            .filter { user: SiteUser -> user.email == email }
            .orElseThrow { CustomException(UserErrorCode.INVALID_USERNAME_OR_EMAIL) }
    }

    private fun generateVerificationCode(): String {
        val random = SecureRandom()
        return String.format("%06d", random.nextInt(1000000))
    }

    private fun sendVerificationEmail(nickname: String, email: String, verificationCode: String) {
        emailService.sendVerificationCode(nickname, email, verificationCode)
    }

    fun verifyAndUnlockAccount(username: String, verificationCode: String) {
        val redisKey = getKey(VERIFICATION_CODE_KEY, username)

        redisService.getValue(redisKey)
            .ifPresentOrElse({ storedCode: String ->
                if (verificationCode != storedCode) {
                    throw CustomException(UserErrorCode.VERIFICATION_CODE_MISMATCH)
                }
                redisService.deleteValue(redisKey)
                redisService.setValue(
                    getKey(PASSWORD_RESET_KEY, username),
                    username,
                    PASSWORD_RESET_EXPIRATION.toLong()
                )
            }, {
                throw CustomException(UserErrorCode.VERIFICATION_CODE_EXPIRED)
            })
    }

    fun changePassword(username: String, password: String) {
        val redisKey = getKey(PASSWORD_RESET_KEY, username)
        val storedUsername = redisService.getValue(redisKey)
            .orElseThrow { CustomException(UserErrorCode.VERIFICATION_CODE_EXPIRED) }

        if (username != storedUsername) {
            redisService.deleteValue(redisKey)
            throw CustomException(UserErrorCode.INVALID_REQUEST)
        }

        val user = userRepository.findByUsername(username)
            .orElseThrow { CustomException(UserErrorCode.USER_NOT_FOUND) }

        user.changePassword(passwordEncoder.encode(password))
        unlockAccount(user)
        redisService.deleteValue(redisKey)
    }

    private fun unlockAccount(user: SiteUser) {
        user.unlockAccount()
        userRepository.save(user)
    }

    private fun getKey(prefix: String, username: String): String {
        return prefix + username
    }


}
