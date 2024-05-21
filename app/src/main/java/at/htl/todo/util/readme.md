# SmallRye config Module

Propably it is not a SmallRye config problem, I found the following Android issue:
https://issuetracker.google.com/issues/153773248

This is from 2020, so i am not sure it will be fixed soon.

The propblematic call comes from line 139 in ClassPathUtils.java:
``` java
try (FileSystem jarFs = FileSystems.newFileSystem(jar, (ClassLoader) null)) {...}
```
