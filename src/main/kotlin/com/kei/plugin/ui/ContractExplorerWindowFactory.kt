package com.kei.plugin.ui

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import com.kei.plugin.core.ContractIndexService
import com.kei.plugin.models.ApiContract
import javax.swing.JPanel
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel
import java.awt.BorderLayout

class ContractExplorerWindowFactory : ToolWindowFactory, DumbAware {
    
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val explorerPanel = ContractExplorerPanel(project)
        val content = ContentFactory.getInstance().createContent(
            explorerPanel,
            "",
            false
        )
        toolWindow.contentManager.addContent(content)
    }
}

class ContractExplorerPanel(private val project: Project) : JPanel(BorderLayout()) {
    
    private val tree: Tree
    private val rootNode = DefaultMutableTreeNode("API Contracts")
    private val treeModel = DefaultTreeModel(rootNode)
    
    init {
        tree = Tree(treeModel)
        tree.isRootVisible = true
        tree.showsRootHandles = true
        
        add(JBScrollPane(tree), BorderLayout.CENTER)
        
        refreshContracts()
    }
    
    fun refreshContracts() {
        rootNode.removeAllChildren()
        
        val service = ContractIndexService.getInstance(project)
        val contracts = service.getAllContracts()
        
        contracts.forEach { contract ->
            addContractNode(contract)
        }
        
        treeModel.reload()
        
        // Expand root
        tree.expandRow(0)
    }
    
    private fun addContractNode(contract: ApiContract) {
        val contractNode = DefaultMutableTreeNode(
            "${contract.fileName}${contract.title?.let { " — $it" } ?: ""}"
        )
        
        // Add endpoints
        contract.endpoints.forEach { endpoint ->
            val endpointNode = DefaultMutableTreeNode(
                "${endpoint.method} ${endpoint.path}"
            )
            contractNode.add(endpointNode)
            
            // Add details
            if (endpoint.summary != null) {
                endpointNode.add(DefaultMutableTreeNode("Summary: ${endpoint.summary}"))
            }
            
            if (endpoint.requestBody != null) {
                val bodyNode = DefaultMutableTreeNode("Request Body")
                endpointNode.add(bodyNode)
                addSchemaNode(bodyNode, endpoint.requestBody.schema)
            }
            
            if (endpoint.responses.isNotEmpty()) {
                val responsesNode = DefaultMutableTreeNode("Responses")
                endpointNode.add(responsesNode)
                endpoint.responses.forEach { response ->
                    val responseNode = DefaultMutableTreeNode("${response.statusCode}: ${response.description ?: ""}")
                    responsesNode.add(responseNode)
                }
            }
        }
        
        rootNode.add(contractNode)
    }
    
    private fun addSchemaNode(parent: DefaultMutableTreeNode, schema: com.kei.plugin.models.Schema) {
        val properties = schema.properties
        if (schema.type == "object" && properties != null) {
            properties.forEach { (name, propSchema) ->
                val required = schema.required?.contains(name) == true
                val requiredMark = if (required) " *" else ""
                val propNode = DefaultMutableTreeNode("$name: ${propSchema.type}$requiredMark")
                parent.add(propNode)
            }
        } else {
            parent.add(DefaultMutableTreeNode("Type: ${schema.type}"))
        }
    }
}
