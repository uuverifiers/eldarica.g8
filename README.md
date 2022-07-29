A [Giter8][g8] template for a starter application that uses [Eldarica](https://github.com/uuverifiers/eldarica).

Template license
----------------
Written in 2022 by uuverifiers.

Apply the template using `sbt new uuverifiers/eldarica.g8`.

In the created template project root directory
- `sbt run` will compile and run the project using sbt.
- `sbt assembly` will use the assembly plugin to create a jar file, which can then be executed using the `run` script under the same directory. A binary distribution could then package the files `run`, `setEnv` and `target/scala-*/*-assembly-*.jar`.

To the extent possible under law, the author(s) have dedicated all copyright and related
and neighboring rights to this template to the public domain worldwide.
This template is distributed without any warranty. See <http://creativecommons.org/publicdomain/zero/1.0/>.

[g8]: http://www.foundweekends.org/giter8/
