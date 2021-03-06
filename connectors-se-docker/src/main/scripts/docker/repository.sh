#! /bin/bash

#
#  Copyright (C) 2006-2018 Talend Inc. - www.talend.com
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#

work_dir="$BASEDIR/target/docker_repository/image"
mkdir -p "$work_dir"
cd "$work_dir"
    grep '^      <artifactId>' "$BASEDIR/pom.xml" | sed "s#.*<artifactId>\(.*\)</artifactId>#\1=org.talend.components:\1:$CONNECTOR_VERSION#" | sort -u > component-registry.properties
    cp "$BASEDIR/src/main/docker/Dockerfile.repository" Dockerfile
    cp -r "$BASEDIR/target/docker-m2" m2

    buildAndTag
    pushImage $LAST_IMAGE
cd -
