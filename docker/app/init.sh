#!/bin/bash

CONTAINER_ALREADY_STARTED="/app/docker/.initialized"
cd /app
if [ ! -e $CONTAINER_ALREADY_STARTED ]; then
    touch $CONTAINER_ALREADY_STARTED
    echo "-- First container startup --"
    touch /app/docker/app/.bash_history
    touch /app/docker/app/.scala_history
    ln -s /app/docker/app/.bash_history /home/app/.bash_history
    ln -s /app/docker/app/.scala_history /home/app/.scala_history
    /bin/bash
else
  /bin/bash
fi

