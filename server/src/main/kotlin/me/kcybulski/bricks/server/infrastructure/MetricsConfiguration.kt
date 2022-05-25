package me.kcybulski.bricks.server.infrastructure

import ratpack.dropwizard.metrics.DropwizardMetricsModule

object MetricsConfiguration {

    fun dropwizardMetricsModule() = DropwizardMetricsModule()
        .also {
            it.configure { config ->
                config
                    .jvmMetrics(true)
                    .console { console ->
                        console.enable(true)
                    }
            }
        }

}
