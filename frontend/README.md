# Fundify

## Getting Started

### Development setup

#### Husky

Run this command once to set up the proper git hooks:

```bash
npx husky init
```

Followed by

```bash
npm run prepare
```

#### Openapi Generator

Todo

#### Angular CLI Generation

e.g.

```bash
ng g s core/Services/OpenApiBeClient
```

# Legacy documentation starts below: ReFOP

## Overview

ReFOp is a tool that aggregates the funding programs and calls data from the endpoints of funding agencies, as
well as provides the possibility to enter the funding programs and calls for the parties that do not maintain
their own funding data repositories and respective endpoints.

The tool is currently being developed by TU Wien as part of the [RIS Synergy](https://forschungsdaten.at/en/ris/)
project.
The funding data is collected from the distributed endpoints of the parties that comply with
the [standards](https://documentation.forschungsdaten.at/) defined within the RIS Synergy project.

The aims of the tool is to:

- offer researchers and research institutions access to the funding opportunities offered by the
  various parties;
- facilitate the creation and promotion of funding calls for the parties that do not maintain their own
  funding calls infrastructure.

ReFOp is composed of following two projects residing in separate source code repositories.

- refop-be: ReFOp [backend](https://github.com/tuwien-csd/refop-be) project
- refop-fe: ReFOp [frontend](https://github.com/tuwien-csd/refop-fe) project

---

These instructions will guide you through setting up the project on your local machine for development and testing
purposes.

### Prerequisites

Before you begin, ensure you have the following tools installed:

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/get-started) and [Docker Compose](https://docs.docker.com/compose/install/)
- [Node.js and npm](https://nodejs.org/) (needs to be compatible with Angular 14, e.g. node v16)
- Java Development Kit (JDK) - preferably the version used by [Quarkus](https://quarkus.io/) (currently JDK 11)

### Setting Up Local Development Environment

#### 1. Clone the Repository

This repository contains the source code for the frontend of ReFOp. It is based on Angular 14.
It is supposed to be run with [refop-be](https://github.com/tuwien-csd/refop-be).

First, clone the repo to your local machine:

```bash
git clone git@github.com:tuwien-csd/refop-fe.git
cd refop-fe
```

#### 2. Start the Application

```bash
nx serve
```

The application will be available at [http://localhost:4200](http://localhost:4200).

If you want to run the application together with the backend, visit [refop-be](https://github.com/tuwien-csd/refop-be)
and follow the instructions there.
