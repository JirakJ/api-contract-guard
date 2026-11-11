package com.kei.plugin.ui.settings

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindSelected
import com.intellij.ui.dsl.builder.panel
import com.kei.plugin.settings.KeiSettings

class KeiSettingsConfigurable(private val project: Project) : BoundConfigurable("Kei API Contract Testing") {

    override fun createPanel(): DialogPanel {
        val state = KeiSettings.getInstance(project).state
        return panel {
            group("Validation") {
                row {
                    checkBox("Validate fetch/axios calls in JavaScript and TypeScript")
                        .bindSelected(state::enableValidation)
                }
                row {
                    checkBox("Warn about calls with no matching contract endpoint")
                        .bindSelected(state::warnOnMissingContract)
                        .comment("Only reported when the project contains at least one contract")
                }
            }
            group("Contract Discovery") {
                row {
                    checkBox("Re-index contract files when they change")
                        .bindSelected(state::autoRefreshContracts)
                        .comment(
                            "Contracts are YAML/JSON files whose name contains 'openapi', 'swagger' or 'api' " +
                                "and that declare an 'openapi' or 'swagger' version",
                        )
                }
            }
        }
    }
}
