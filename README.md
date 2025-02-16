# TempTake MQTT

## About

This is the MQTT module for the TempTake project. The MQTT module is responsible for receiving data from the manager module and publishing it to the MQTT broker.

## Supported topics

- `temptake/entry`
- `temptake/manager/register`
- `temptake/manager/unregister`
- `temptake/worker/register`
- `temptake/worker/unregister`

## Installation and setup

The MQTT service will start in the docker container of the project.
