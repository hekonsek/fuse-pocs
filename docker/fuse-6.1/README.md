# JBoss Fuse 6.1 Docker image


JBoss Fuse in not distributed using Docker images. If you would like to play with dockerized Fuse 6.1, you need
to build the image of the latter by yourself. Or using this project :) .

## Building Fuse 6.1 Docker image

Download this project from GitHub:

    git clone git@github.com:hekonsek/fuse-pocs.git

Navigate to the Dockerfile directory:

    cd docker/fuse-6.1

Execute `build.sh` script:

    % ./build.sh
    Can't find Fuse 6.1 installer: /home/hekonsek/opt/fuse_installers/jboss-fuse-full-6.1.0.redhat-379.zip

Ops, we forgot to download Fuse installer! Go to the
[Fuse 6.1 download page](https://access.redhat.com/jbossnetwork/restricted/softwareDetail.html?softwareId=29253&product=jboss.fuse&version=6.1.0&downloadType=distributions)
and download it. Then put `jboss-fuse-full-6.1.0.redhat-379.zip` file to the `~/opt/fuse_installers` directory. And
try to build the Docker image again:

    % ./build.sh
    Sending build context to Docker daemon 633.3 MB
    Sending build context to Docker daemon
    ...
    Successfully built c98dc9e7d80c
    ==================================================================================================
    Successfully created Fuse 6.1 Docker image.
    ==================================================================================================

Yay! We've just successfully created the Fuse 6.1 Docker image!

## Running Fuse 6.1 Docker container

You can start Fuse as background server daemon using the following command:

    docker run -d -t fuse/6.1

You can also start Fuse server in the interactive mode (with shell opened):

    docker run -it fuse/6.1 fuse
