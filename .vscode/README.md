# VS Code Quick Notes

Steps to run and fix common issues for this project:

- Clean Java language server: Ctrl+Shift+P → `Java: Clean the Java language server workspace` and accept restart.
- Reimport Maven: View → `Maven Projects` → right-click project → `Update Project`.
- Build from terminal:

```powershell
cd "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest"
Remove-Item -Recurse -Force target/ -ErrorAction SilentlyContinue
$env:PATH = "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest\scripts\tools\apache-maven-3.9.11\bin;$env:PATH"
mvn -DskipTests clean compile
```

- Run from VS Code: Run and Debug → select `Launch Main`.

If JavaFX modules warnings appear, the launch configuration already adds `--enable-native-access=javafx.graphics`.
