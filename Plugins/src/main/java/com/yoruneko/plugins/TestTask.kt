package com.yoruneko.plugins

import com.android.build.api.variant.impl.ApplicationVariantBuilderImpl
import com.android.build.api.variant.impl.ApplicationVariantImpl
import com.android.build.api.variant.impl.VariantImpl
import com.android.build.gradle.internal.publishing.AndroidArtifacts
import com.android.build.gradle.internal.tasks.AndroidVariantTask
import com.android.build.gradle.internal.tasks.factory.TaskCreationAction
import com.android.build.gradle.internal.variant.ComponentInfo
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class TestTask : AndroidVariantTask() {
//    val inputs: Property<FileCollection>

    @get:Input
    abstract val component: Property<VariantImpl>

    @TaskAction
    fun test() {
        val fileCollection = project.files()
        // aar
//        fileCollection.from(
//            component.get().variantDependencies.getArtifactCollection(
//                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
//                AndroidArtifacts.ArtifactScope.EXTERNAL,
//                AndroidArtifacts.ArtifactType.CLASSES
//            ).artifactFiles
//        )
        component.get().variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.EXTERNAL,
            AndroidArtifacts.ArtifactType.CLASSES
        ).artifactFiles.forEach {
            println("zty: runtime + classes + external: " + it.name)
        }

        component.get().variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.PROJECT,
            AndroidArtifacts.ArtifactType.CLASSES
        ).artifactFiles.forEach {
            println("zty: runtime + classes + project: " + it.name)
        }

        component.get().variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.PROJECT,
            AndroidArtifacts.ArtifactType.AAR
        ).artifactFiles.forEach {
            println("zty: runtime + aar + project: " + it.name)
        }


        component.get().variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
            AndroidArtifacts.ArtifactScope.EXTERNAL,
            AndroidArtifacts.ArtifactType.AAR
        ).artifactFiles.forEach {
            println("zty: runtime + aar + external: " + it.name)
        }

        component.get().variantDependencies.getArtifactCollection(
            AndroidArtifacts.ConsumedConfigType.COMPILE_CLASSPATH,
            AndroidArtifacts.ArtifactScope.EXTERNAL,
            AndroidArtifacts.ArtifactType.CLASSES
        ).artifactFiles.forEach {
            println("zty: api + classes + external: " + it.name)
        }


//        fileCollection.from(
//            component.get().variantDependencies.getArtifactCollection(
//                AndroidArtifacts.ConsumedConfigType.RUNTIME_CLASSPATH,
//                AndroidArtifacts.ArtifactScope.PROJECT,
//                AndroidArtifacts.ArtifactType.CLASSES
//            ).artifactFiles
//        )
//        fileCollection.forEach {
//            println("zty: " + it.absolutePath)
//        }
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
//            task.inputs.set(fileCollection)

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
//            task.inputs.set(fileCollection)

            task.dependsOn(project.tasks.getByName(component.variant.computeTaskName("compile", "JavaWithJavac")))
        }

        override val name: String
            get() = "test${component.variant.name}Task"
        override val type: Class<TestTask>
            get() = TestTask::class.java

    }
}