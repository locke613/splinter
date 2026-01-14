# splinter
Powerful linter tool for spark application

## Prerequisites

This tool requires Java and sbt. The recommended way to install them is via [SDKMAN!](https://sdkman.io).

```bash
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.10-tem
sdk install sbt
```

## Usage

**Run Linter:**
```bash
sbt "run demo.scala"
```

**Run Linter with Fixes:**
```bash
sbt "run --fix demo.scala"
```

**Run Unit tests:**
```bash
sbt test
```

**Build jar**
```bash
sbt assembly
```

**Run the tool**
```bash
java -jar splinter.jar file.scala
```
