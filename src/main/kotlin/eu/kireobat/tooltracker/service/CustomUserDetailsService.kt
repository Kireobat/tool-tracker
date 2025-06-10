package eu.kireobat.tooltracker.service

import eu.kireobat.tooltracker.persistence.entity.UserEntity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrElse

@Service
class CustomUserDetailsService(
    private val userService: UserService,
    private val userMapRoleService: UserMapRoleService
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val user = userService.findByEmail(email)
            .getOrElse { throw UsernameNotFoundException("User not found with email: $email") }
        return CustomUserDetails(user, userMapRoleService.getRoles(user.id))
    }
}

class CustomUserDetails(private val user: UserEntity,
                        private val roles: List<SimpleGrantedAuthority>) : UserDetails {
    override fun getAuthorities() = roles
    override fun getPassword() = user.passwordHash
    override fun getUsername() = user.email
    fun getName() = user.name
}