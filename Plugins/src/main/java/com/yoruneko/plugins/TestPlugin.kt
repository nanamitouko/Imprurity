package com.yoruneko.plugins

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            val taskFactory = TaskFactoryImpl(project.tasks)
            project.plugins.getPlugin(AppPlugin::class.java).variantManager.mainComponents.forEach {
                taskFactory.register(TestTask.ConfigAction(it, project))
            }
        }
    }


}