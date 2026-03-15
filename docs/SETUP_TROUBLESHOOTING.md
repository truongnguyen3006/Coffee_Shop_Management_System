# Setup Guide & Common Troubleshooting

For a public GitHub portfolio repository, this document should be written fully in **English** to keep the project consistent and easier for recruiters or reviewers to read.

## 1. JavaFX does not run

### Symptoms
- The IDE reports missing `javafx.*` packages.
- Running `Main.java` fails with a module path or JavaFX-related error.

### How to fix it
- Install JavaFX SDK 25 separately.
- Add the JavaFX SDK libraries to your IDE.
- Set the JavaFX VM options correctly.

Example VM options:

```bash
--module-path "C:\path\to\javafx-sdk-25\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base
```

## 2. Cannot connect to MySQL

### Check
- Is MySQL running?
- Has the `coffee` database been created?
- Is `app.properties` configured correctly?

### Suggested fix
- Import `sql/schema.sql`
- Import `sql/seed-lite.sql` for quick testing

## 3. RabbitMQ does not run on Windows

### Prerequisites
- Install Erlang/OTP first.
- Then install RabbitMQ.

### Quick checks
- Does the RabbitMQ service exist?
- Is port `5672` open?
- Are the default username and password correct?

### Notes
If the RabbitMQ service fails after installation, the common reasons are:
- incompatible RabbitMQ and Erlang versions
- installation path issues or service configuration problems
- old RabbitMQ services were not removed cleanly

### Suggested fix
- Remove the old RabbitMQ service
- Verify the Erlang version
- Reinstall RabbitMQ
- Start the RabbitMQ service before launching the application

## 4. Cannot send email

### Check
- Are `mail.username` and `mail.password` configured correctly?
- If you use Gmail, are you using a Gmail App Password instead of a normal password?
- Is SMTP blocked on the current machine or network?

## 5. The UI breaks when running on another machine

This part has already been improved by:
- removing absolute paths such as `D:/...`
- loading images, icons, CSS, and FXML from project resources

If the problem still happens, the common reasons are:
- the IDE source/resource path is not configured correctly
- JavaFX SDK is not attached correctly
- required libraries inside `lib/` were not added

## 6. Recommended quick demo setup

To demo the project on a new machine, follow this order:

1. Install JDK
2. Install JavaFX SDK
3. Install MySQL
4. Import `schema.sql` and `seed-lite.sql`
5. Install Erlang/OTP and RabbitMQ
6. Create `app.properties`
7. Run `Main.java`

## Common setup issues

### `Module javafx.controls not found`
Check that:
- JavaFX SDK 25 is installed
- the correct JavaFX `lib` folder is used in `--module-path`
- the VM options are configured correctly

### Images or icons are not displayed
Make sure the project keeps the original folder and resource structure when imported into the IDE.

### Email sending failed
Check:
- SMTP host and port
- Gmail App Password
- `mail.username` and `mail.password` values in `app.properties`

### RabbitMQ chat does not work
Make sure the RabbitMQ service is running before launching the application.

### The application runs on one machine but fails on another
Check:
- JavaFX SDK path
- `app.properties`
- MySQL and RabbitMQ availability
- manually added libraries inside `lib/`
