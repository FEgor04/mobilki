import com.koji.AppLogger
import com.koji.auth.dto.ApiResponse
import com.koji.auth.dto.SignInRequest
import com.koji.auth.dto.SignUpRequest
import com.koji.auth.repository.UserRepository
import com.koji.auth.security.PasswordService
import com.koji.auth.security.TokenConfig
import com.koji.auth.service.AuthService
import com.koji.exceptions.DatabaseOperationException
import com.koji.exceptions.EmailFormatException
import com.koji.exceptions.InvalidCredentialsException
import com.koji.exceptions.UserAlreadyExistsException
import com.koji.exceptions.UserNotFoundException
import com.koji.exceptions.WeakNameException
import com.koji.exceptions.WeakPasswordException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes() {
    val logger = AppLogger.getLogger("AuthRoutes")
    val userRepository = UserRepository()
    val passwordService = PasswordService()

    val config = application.environment.config
    val tokenConfig = TokenConfig(
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
        expiresIn = 1000L * 60L * 60L * 24L * 7L, // 1 week
        secret = config.property("jwt.secret").getString(),
        realm = config.property("jwt.realm").getString()
    )

    val authService = AuthService(userRepository, passwordService, tokenConfig)

    route("/api/auth") {
        post("/signup") {
            try {
                val request = call.receive<SignUpRequest>()
                val result = authService.signUp(request)
                call.respond(HttpStatusCode.Created, ApiResponse.Success(result))
            } catch (e: UserAlreadyExistsException) {
                logger.warn("Signup failed: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: EmailFormatException) {
                logger.warn("Invalid email format: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: WeakPasswordException) {
                logger.warn("Weak password: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: WeakNameException) {
                logger.warn("Weak name: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: DatabaseOperationException) {
                logger.error("Database error during signup: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: Exception) {
                logger.error("Unexpected error during signup: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.Error("Could not register user"))
            }
        }

        post("/signin") {
            try {
                val request = call.receive<SignInRequest>()
                val result = authService.signIn(request)
                call.respond(HttpStatusCode.OK, ApiResponse.Success(result))
            } catch (e: InvalidCredentialsException) {
                logger.warn("Sign in failed: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: UserNotFoundException) {
                logger.warn("User not found during sign in: ${e.message}")
                call.respond(e.statusCode, ApiResponse.Error(e.message))
            } catch (e: Exception) {
                logger.error("Unexpected error during sign in: ${e.message}", e)
                call.respond(HttpStatusCode.InternalServerError, ApiResponse.Error("Could not authenticate user"))
            }
        }
    }
}