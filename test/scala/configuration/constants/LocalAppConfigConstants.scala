package configuration.constants

import configuration.models.*
import configuration.AppConfig

object LocalAppConfigConstants {

  val appServerConfig =
    ServerConfig(
      host = "0.0.0.0",
      port = 8080
    )

  val featureSwitches =
    FeatureSwitches(
      useDockerHost = false,
      localTesting = false,
      useCors = false,
      useHttpsLocalstack = true,
      useProdStripe = false
    )

  val kafkaConfig =
    KafkaConfig(
      bootstrapServers = "localhost:9092",
      clientId = "dev-irl-client-payment-service",
      acks = "all",
      lingerMs = 5,
      retries = 10,
      topic = KafkaTopicConfig(
        "quest.created.v1",
        "esimtation.finalized.v1"
      )
    )

  val devIrlFrontendConfig =
    DevIrlFrontendConfig(
      host = "0.0.0.0",
      port = 3000,
      baseUrl = "http://localhost:3000"
    )

  val containerPostgreSqlConfig =
    PostgresqlConfig(
      dbName = "dev_quest_db",
      dockerHost = "dev-quest-container",
      host = "localhost",
      port = 5432,
      username = "dev_quest_user",
      password = "turnip",
      maxPoolSize = 42
    )

  val redisConfig =
    RedisConfig(
      dockerHost = "redis-container",
      host = "localhost",
      port = 6379
    )

  val stripeConfig =
    StripeConfig(
      registrationRefreshUrl = "http://localhost:3000/dev/stripe/onboarding/refresh",
      registrationReturnUrl = "http://localhost:3000/dev/stripe/onboarding/success",
      paymentSuccessUrl = "http://localhost:3000/payment/success",
      paymentCancelUrl = "http://localhost:3000/payment/error",
      stripeUrl = "https://api.stripe.com/v1",
      platformFeePercent = 2.5
    )

  val localAppConfigConstant =
    AppConfig(
      devIrlFrontendConfig = devIrlFrontendConfig,
      featureSwitches = featureSwitches,
      kafka = kafkaConfig,
      postgresqlConfig = containerPostgreSqlConfig,
      redisConfig = redisConfig,
      serverConfig = appServerConfig,
      stripeConfig = stripeConfig
    )
}
