package com.ll.TeamProject.global.initData

import com.ll.TeamProject.standard.util.Cmd
import com.ll.TeamProject.standard.util.File
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class DevInitData {

    @Autowired
    @Lazy
    lateinit var self: DevInitData

    @Bean
    fun devInitDataApplicationRunner(): ApplicationRunner {
        return ApplicationRunner { _: ApplicationArguments? ->
            try {
                File.downloadByHttp("http://localhost:8080/v3/api-docs", ".")

                val cmd =
                    "yes | npx --package typescript --package openapi-typescript openapi-typescript api-docs.json -o ../frontend/src/lib/backend/schema.d.ts"
                Cmd.runAsync(cmd)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
