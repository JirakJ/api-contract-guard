package com.kei.plugin.ui.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.*
import javax.swing.JComponent

class KeiSettingsConfigurable : Configurable {
    
    private var panel: DialogPanel? = null
    private var enableValidation = true
    private var showWarnings = true
    private var autoRefreshContracts = true
    
    override fun getDisplayName() = "Kei API Contract Testing"
    
    override fun createComponent(): JComponent {
        panel = panel {
            group("Validation") {
                row {
                    checkBox("Enable real-time validation")
                        .bindSelected(::enableValidation)
                }
                row {
                    checkBox("Show warnings for missing contracts")
                        .bindSelected(::showWarnings)
                }
            }
            
            group("Contract Discovery") {
                row {
                    checkBox("Auto-refresh contracts on file changes")
                        .bindSelected(::autoRefreshContracts)
                }
                row {
                    label("Contract file patterns:")
                }
                row {
                    textField()
                        .columns(40)
                        .text("**/openapi.{yaml,yml,json}, **/swagger.{yaml,yml,json}")
                        .enabled(false)
                        .comment("Default patterns for contract discovery")
                }
            }
            
            group("Features") {
                row {
                    label("Features (free & open source):")
                }
                row {
                    label("✓ OpenAPI 3.x contract discovery")
                }
                row {
                    label("✓ Real-time validation (TypeScript, JavaScript)")
                }
                row {
                    label("✓ Basic quick fixes")
                }
                row {
                    label("✓ Contract Explorer")
                }
            }
        }
        
        return panel!!
    }
    
    override fun isModified(): Boolean {
        return panel?.isModified() ?: false
    }
    
    override fun apply() {
        panel?.apply()
    }
    
    override fun reset() {
        panel?.reset()
    }
}
