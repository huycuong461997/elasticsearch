import jetbrains.buildServer.configs.kotlin.v2019_2.Project
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.versionedSettings
import jetbrains.buildServer.configs.kotlin.v2019_2.vcs.GitVcsRoot

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

val developmentBranches = listOf("master", "7.x", "7.8", "6.8")

fun Project.includeDevelopmentBranchProjects() {
    subProjectsOrder = developmentBranches.map { branch ->
        subProject {
            id(branch.replace('.', '_'))
            name = branch

            val kotlinDslRoot = createVcsRoot(name, "${branch}_teamcity")
            vcsRoot(createVcsRoot(name, branch))
            vcsRoot(kotlinDslRoot)

            features {
                versionedSettings {
                    rootExtId = kotlinDslRoot.id.toString()
                    mode = VersionedSettings.Mode.ENABLED
                    buildSettingsMode = VersionedSettings.BuildSettingsMode.PREFER_SETTINGS_FROM_VCS
                    settingsFormat = VersionedSettings.Format.KOTLIN
                }
            }
        }
    }.map { it.id!! }
}

fun createVcsRoot(projectName: String, branchName: String): GitVcsRoot {
    return GitVcsRoot {
        id("${projectName}_${branchName.replace('.', '_')}")

        name = "${projectName} ($branchName)"
        url = "https://github.com/elastic/${projectName.toLowerCase()}.git"
        branch = "refs/heads/$branchName"
    }
}