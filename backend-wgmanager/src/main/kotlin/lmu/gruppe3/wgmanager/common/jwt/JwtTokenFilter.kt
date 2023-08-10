package lmu.gruppe3.wgmanager.common.jwt

import lmu.gruppe3.wgmanager.auth.dto.PrincipalDto
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtTokenFilter(private val jwtUtil: JwtUtil) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtUtil.getJwtFromRequest(request)
        if (token != null && jwtUtil.getEmailByToken(token) != null) {
            setAuthenticationContext(token, request)
        }
        filterChain.doFilter(request, response)
    }

    private fun setAuthenticationContext(token: String, request: HttpServletRequest) {
        val principal: PrincipalDto = getUserDetails(token)
        val authentication = UsernamePasswordAuthenticationToken(principal, null, null)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun getUserDetails(token: String): PrincipalDto {
        val email = this.jwtUtil.getEmailByToken(token)
        val userId = this.jwtUtil.getUserIdByToken(token)
        return PrincipalDto(email = email ?: "", userId = UUID.fromString(userId ?: ""))
    }
}