package com.kei.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project

/**
 * Per-project Kei settings (stored in the project's `.idea/kei.xml`).
 */
@Service(Service.Level.PROJECT)
@State(name = "KeiSettings", storages = [Storage("kei.xml")])
class KeiSettings : PersistentStateComponent<KeiSettings.State> {

    data class State(
        /** Run the API contract inspection on JavaScript/TypeScript calls. */
        var enableValidation: Boolean = true,
        /** Warn about calls whose endpoint is not described by any indexed contract. */
        var warnOnMissingContract: Boolean = true,
        /** Re-index contract files automatically when they change on disk. */
        var autoRefreshContracts: Boolean = true,
    )

    private var myState = State()

    override fun getState(): State = myState

    override fun loadState(state: State) {
        myState = state
    }

    companion object {
        fun getInstance(project: Project): KeiSettings = project.getService(KeiSettings::class.java)
    }
}
