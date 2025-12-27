# Exécution simple

Deux helpers se trouvent à la racine pour faciliter l'exécution sous Windows :

- PowerShell : `run.ps1`
- Invite de commandes (CMD) : `run.bat`

Comportement :
- Les scripts cherchent d'abord `mvn` dans le PATH système.
- Si `mvn` n'est pas présent, ils utilisent une copie locale si elle existe sous `scripts/tools/apache-maven-3.9.11`.
- Si aucun Maven n'est trouvé, le script affiche un message expliquant comment installer Maven.

Exemples (PowerShell) :
```powershell
cd "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest"
.\run.ps1                     # lance `mvn -DskipTests javafx:run`
.\run.ps1 clean compile       # compile sans lancer
.\run.ps1 javafx:run -Djavafx.platform=win  # force la plateforme Windows si nécessaire
```

Exemples (CMD) :
```
cd "C:\Users\nasri\OneDrive\Desktop\4eme\JEE\Jumpio quest"
run.bat                        # lance `mvn -DskipTests javafx:run`
run.bat clean compile          # compile sans lancer
run.bat javafx:run -Djavafx.platform=win
```

Note: These helpers simplify running the project on other machines without modifying `pom.xml`. For a fully portable solution, consider adding the Maven Wrapper (`mvnw`), which I can generate for you if desired.
