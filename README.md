<div align="center">
<img src="https://raw.githubusercontent.com/quarkiverse/.github/refs/heads/main/assets/images/quarkus.svg" width="67" height="70" >
<img src="https://raw.githubusercontent.com/quarkiverse/.github/refs/heads/main/assets/images/plus-sign.svg" height="70" >
<img src="https://oras.land/img/oras.svg" height="70" >
</div>

# Quarkus ORAS

> [!WARNING]
> The Oras Java SDK is currently in **alpha** state.
>
> It's configuration and APIs might change in future releases

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=flat-square)](https://opensource.org/licenses/Apache-2.0)

## Introduction

Quarkus ORAS is a Quarkus extension for the [ORAS Java SDK](https://github.com/oras-project/oras-java) library.
ORAS is a library to manage OCI artifacts into an OCI compliant registry.

The main purpose of this extension is to make ORAS work in a native executable built with GraalVM/Mandrel and support auto-configuration and dependency injection.
