package services

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import cats.data.ValidatedNel
import cats.effect.Async
import cats.effect.Concurrent
import cats.implicits.*
import cats.syntax.all.*
import cats.Monad
import cats.NonEmptyParallel
import connectors.*
import fs2.Stream
import java.util.UUID
import models.database.*
import models.users.*
import models.UserType
import org.typelevel.log4cats.Logger

trait UserDataServiceAlgebra[F[_]] {

  def getUser(userId: String): F[Option[UserData]]
}

class UserDataServiceImpl[F[_] : Async : Logger](
  authConnector: AuthConnector[F]
) extends UserDataServiceAlgebra[F] {

  override def getUser(userId: String): F[Option[UserData]] =
    authConnector.getUserProfile(userId).flatMap {
      case Some(user) =>
        Logger[F].debug(s"[UserDataService] Found user with ID: $userId") *> Concurrent[F].pure(Some(user))
      case None =>
        Logger[F].debug(s"[UserDataService] No user found with ID: $userId") *> Concurrent[F].pure(None)
    }
}

object UserDataService {

  def apply[F[_] : Async : Logger](authConnector: AuthConnector[F]): UserDataServiceAlgebra[F] =
    new UserDataServiceImpl[F](authConnector)
}
