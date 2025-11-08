package configuration.constants

import configuration.models.*
import configuration.AppConfig

object ProdAppConfigConstants {

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
      clientId = "dev-irl-hiscore-service",
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
      port = 8080,
      baseUrl = "https://devirl.com"
    )

  val appServerConfig =
    ServerConfig(
      host = "0.0.0.0",
      port = 8080
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
      registrationRefreshUrl = "https://devirl.com/dev/stripe/onboarding/refresh",
      registrationReturnUrl = "https://devirl.com/dev/stripe/onboarding/success",
      paymentSuccessUrl = "https://devirl.com/payment/success",
      paymentCancelUrl = "https://devirl.com/payment/error",
      stripeUrl = "https://api.stripe.com/v1",
      platformFeePercent = 2.5
    )

  val prodAppConfigConstant =
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
