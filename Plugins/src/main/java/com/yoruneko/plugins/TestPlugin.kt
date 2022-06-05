package com.yoruneko.plugins

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.internal.plugins.AppPlugin
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.factory.TaskFactoryImpl
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project

class TestPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.afterEvaluate {
            val taskFactory = TaskFactoryImpl(project.tasks)
            project.extensions.getByType(AndroidComponentsExtension::class.java).onVariants {
                project.tasks.register("tt${it.name}Task") {
                    print("test zty: ${it.name}!")
                }
                val task = project.tasks.register("w${it.name}Task", TestTask::class.java) { task ->
                    task.component.set(it as VariantImpl)
                }
//                task.dependsOn((it as VariantImpl).computeTaskName("assemble"))
//                project.tasks.getByName("assemble").finalizedBy(task)
                task.dependsOn(project.tasks.getByName("assemble"))
//                taskFactory.register(TestTask.ConfigAction2(it as VariantImpl, project))
            }

//            project.extensions.getByType(AndroidComponentsExtension::class.java).beforeVariants {
//                val task = project.tasks.register("tt${it.name}Task2") {
//                    print("test zty: ${it.name}!")
//                }
//                task.dependsOn("compile${it.name}JavaWithJavac")
//            }
//            project.plugins.getPlugin(AppPlugin::class.java).variantManager.mainComponents.forEach {
//                taskFactory.register(TestTask.ConfigAction(it, project))
//            }
        }
    }


}