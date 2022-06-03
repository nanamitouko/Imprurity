package com.yoruneko.plugins

import com.android.build.api.variant.ApplicationVariantBuilder
import com.android.build.api.variant.Variant
import com.android.build.api.variant.VariantBuilder
import com.android.build.api.variant.impl.ApplicationVariantBuilderImpl
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.VariantBuilderImpl
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import com.android.build.gradle.internal.component.ComponentCreationConfig
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.scope.VariantScope
import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import com.android.build.gradle.internal.tasks.factory.VariantTaskCreationAction
import com.android.build.gradle.internal.variant.ComponentInfo
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class TestTask : AndroidVariantTask() {
    @get:Input
    abstract val inputs: Property<FileCollection>

    @TaskAction
    fun test() {
        inputs.get().forEach {
            println("zty: " + it.absolutePath)
        }
    }

    class ConfigAction2 constructor(
        private val component: VariantImpl,
        private val project: Project
    ) : TaskCreationAction<TestTask>() {

        override fun configure(task: TestTask) {
            val fileCollection = project.files()
            // aar
            fileCollection.from(
                component.variantDependencies.getArtifactCollection(
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    AndroidArtifacts.ArtifactScope.EXTERNAL,
                    AndroidArtifacts.ArtifactType.CLASSES
                )
            )

            fileCollection.from(
                component.variantDependencies.getArtifactCollection(
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    AndroidArtifacts.ArtifactScope.PROJECT,
                    AndroidArtifacts.ArtifactType.CLASSES
                )
            )
            task.inputs.set(fileCollection)

            task.dependsOn(project.tasks.getByName(component.computeTaskName("compile", "JavaWithJavac")))
        }

        override val name: String
            get() = "test${component.name}Task"
        override val type: Class<TestTask>
            get() = TestTask::class.java

    }

    class ConfigAction constructor(
        private val component: ComponentInfo<ApplicationVariantBuilderImpl, ApplicationVariantImpl>,
        private val project: Project
    ) : TaskCreationAction<TestTask>() {
        override fun configure(task: TestTask) {
            val fileCollection = project.files()
            // aar
            fileCollection.from(
                component.variant.variantDependencies.getArtifactCollection(
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    AndroidArtifacts.ArtifactScope.EXTERNAL,
                    AndroidArtifacts.ArtifactType.CLASSES
                )
            )

            fileCollection.from(
                component.variant.variantDependencies.getArtifactCollection(
                    AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
                    AndroidArtifacts.ArtifactScope.PROJECT,
                    AndroidArtifacts.ArtifactType.CLASSES
                )
            )
            task.inputs.set(fileCollection)

            task.dependsOn(project.tasks.getByName(component.variant.computeTaskName("compile", "JavaWithJavac")))
        }

        override val name: String
            get() = "testTask"
        override val type: Class<TestTask>
            get() = TestTask::class.java

    }
}