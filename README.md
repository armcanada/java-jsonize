# Jsonize

>ARM Canada inc.

Library for easy json formatting

## Usage

Pass any kind of object into static method `convert` to gets it's json format string.
### Object
```java
class User
{
    private String firstname;
    private String lastname;
    private int age;
    public User(String firstname, String lastname, int age)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }
}

public static class Main
{
    public static void main()
    {
        String json = Json.convert(new User("John", "Doe", 18));
        /* 
            {
                "firstname":"John",
                "lastname":"Doe",
                "age":18
            }
        */
    }
}
```
### Object with nested object
You can also have nested objects to format
```java
class User
{
    private String firstname;
    private String lastname;
    private int age;
    private Job job;
    public User(String firstname, String lastname, int age, Job job)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.job = job;
    }
}
class Job
{
    private String name;
    public Job(String name)
    {
        this.name = name;
    }
}
public static class Main
{
    public static void main()
    {
        String json = Json.convert(new User("John", "Doe", 18, new Job("Developer")));
        /*
            {
                "firstname":"John",
                "lastname":"Doe",
                "age":18,
                "job": {
                    "name":"Developer"
                }
            }
         */
    }
}
```

### Object with collection
You can also have nested collections to format
```java
class User
{
    private String firstname;
    private String lastname;
    private int age;
    private List<Job> jobs;
    public User(String firstname, String lastname, int age)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }
    public void addJob(Job job)
    {
        this.jobs.add(job);
    }
}
class Job
{
    private String name;
    public Job(String name)
    {
        this.name = name;
    }
}
public static class Main
{
    public static void main()
    {
        User user = new User("John", "Doe", 18);
        user.addJob(new Job("Developer"));
        user.addJob(new Job("Magician"));
        String json = Json.convert(user);
        /*
            {
                "firstname":"John",
                "lastname":"Doe",
                "age":18,
                "jobs":[
                    {
                        "name":"Developer"
                    },{
                        "name":"Magician"
                    }
                ]
            }
         */
    }
}
```

### Hide field
The package also includes an annotation @DontJson that you can add to your field if you want to prevent them from being formatted
```java
class User
{
    private String firstname;
    private String lastname;
    private int age;
    @DontJson
    private String password;
    public User(String firstname, String lastname, int age, String password)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.password = password;
    }
}

public static class Main
{
    public static void main()
    {
        String json = Json.convert(new User("John", "Doe", 18, "super secret stuff"));
        
        /* 
            {
                "firstname":"John",
                "lastname":"Doe",
                "age":18
            }
        */
    }
}
```
### Append getters
You may need to append a method call to your json string like for a computed information. You annotate your method with @AppendJson to have it called on formatting. You can return any kind of object as they will be passed through the json converter. If you set the annotation key property as below, the property will be use as the key, else it'll use the method name.
```java
class User
{
    private String firstname;
    private String lastname;
    private int age;
    public User(String firstname, String lastname, int age)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }
    @AppendJson(key = "fullname")
    public String getFullname()
    {
        return String.format("%s %s", this.firstname, this.lastname);
    }
}

public static class Main
{
    public static void main()
    {
        String json = Json.convert(new User("John", "Doe", 18));
        /* 
            {
                "firstname":"John",
                "lastname":"Doe",
                "age":18,
                "fullname:"John Doe"
            }
        */
    }
}
```