package lmu.gruppe3.wgmanager.common.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import lmu.gruppe3.wgmanager.user.dto.UserDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*
import javax.servlet.http.HttpServletRequest

@Component
class JwtUtil {
    @Value("\${app.jwt.secret}")
    private lateinit var JWT_SECRET: String


    fun generateJwtToken(user: UserDto): String? {
        val expiryDate = Calendar.getInstance()
        expiryDate.add(Calendar.DAY_OF_MONTH, 1)
        return JWT.create()
            .withSubject("${user.id}")
            .withIssuedAt(Date())
            .withExpiresAt(expiryDate.time)
            .withClaim("userId", user.id.toString())
            .withClaim("email", user.email)
            .sign(Algorithm.HMAC256(this.JWT_SECRET))
    }

    fun decodeJwtToken(token: String): DecodedJWT {
        return JWT.require(Algorithm.HMAC256(this.JWT_SECRET)).build().verify(token)
    }

    fun getEmailByToken(token: String): String? {
        return this.decodeJwtToken(token).getClaim("email").asString()
    }

    fun getUserIdByToken(token: String): String? {
        return this.decodeJwtToken(token).getClaim("userId").asString()
    }

    fun getJwtFromRequest(request: HttpServletRequest): String? {
        val requestTokenHeader: String = request.getHeader("Authorization") ?: return null
        if (requestTokenHeader.isBlank()) {
            return null
        }
        val tokenSplit = requestTokenHeader.split(" ".toRegex()).toTypedArray()
        if (tokenSplit.size == 2) {
            val tokenPrefix = tokenSplit[0]
            if ("Bearer" == tokenPrefix) {
                return tokenSplit[1]
            }
        }
        return null
    }


}