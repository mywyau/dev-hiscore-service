package configuration

import cats.kernel.Eq
import configuration.models.*
import pureconfig.ConfigReader
import pureconfig.generic.derivation.*

case class AppConfig(
  featureSwitches: FeatureSwitches,
  devIrlFrontendConfig: DevIrlFrontendConfig,
  kafka: KafkaConfig,
  postgresqlConfig: PostgresqlConfig,
  redisConfig: RedisConfig,
  serverConfig: ServerConfig,
  stripeConfig: StripeConfig
) derives ConfigReader
