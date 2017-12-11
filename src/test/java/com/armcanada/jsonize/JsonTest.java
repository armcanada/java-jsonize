package com.armcanada.jsonize;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Mock classes
 */
class DontJsonUser {
    private String firstname;
    private String lastname;
    private int age;
    @DontJson
    private String password;
    public DontJsonUser(String firstname, String lastname, int age, String password)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.password = password;
    }
}
class User {
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
class Job {
    private String name;
    public Job(String name)
    {
        this.name = name;
    }
}
class Department {
    private String name;
    private Job job;
    public Department(String name, Job job)
    {
        this.name = name;
        this.job = job;
    }
}
class NestedUser  {
    private String firstname;
    private String lastname;
    private int age;
    private Department department;
    public NestedUser(String firstname, String lastname, int age, Department department)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.department = department;
    }
}
class AppendUser {
    private String firstname;
    private String lastname;
    private int age;
    public AppendUser(String firstname, String lastname, int age)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    @AppendJson(key = "email")
    public String getEmail()
    {
        return "jdoe@email.com";
    }
}
class CollectionUser {
    private String firstname;
    private String lastname;
    private int age;
    private List<Job> jobs = new ArrayList<>();
    public CollectionUser(String firstname, String lastname, int age)
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
class ArrayUser {
    private String firstname;
    private String lastname;
    private int age;
    private Job[] jobs;
    @DontJson
    private int jobCount = 0;
    public ArrayUser(String firstname, String lastname, int age, int jobSize)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.jobs = new Job[jobSize];
    }

    public void addJob(Job job)
    {
        this.jobs[this.jobCount++] = job;
    }
}

public class JsonTest
{
    @Test
    public void testStringConvert() throws Exception
    {
        assertEquals("\"Test string\"", Json.convert("Test string"));
    }

    @Test
    public void testSafeStringConvert() throws Exception
    {
        assertEquals("\"Test a \\\"safe\\\" string\"", Json.convert("Test a \"safe\" string"));
    }

    @Test
    public void testNumberConvert() throws Exception
    {
        assertEquals("2.12", Json.convert(2.12));
    }

    @Test
    public void testObjectConvert() throws Exception
    {
        User user = new User("John", "Doe", 18);
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18}", Json.convert(user));
    }

    @Test
    public void testObjectConvertWithDontJson() throws Exception
    {
        DontJsonUser user = new DontJsonUser("John", "Doe", 18, "secret");
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18}", Json.convert(user));
    }

    @Test
    public void testConvertWithAppend() throws Exception
    {
        AppendUser user = new AppendUser("John", "Doe", 18);
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18,\"email\":\"jdoe@email.com\"}", Json.convert(user));
    }

    @Test
    public void testCollectionConvert() throws Exception
    {
        CollectionUser user = new CollectionUser("John", "Doe", 18);
        user.addJob(new Job("Developer"));
        user.addJob(new Job("Magician"));
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18,\"jobs\":[{\"name\":\"Developer\"},{\"name\":\"Magician\"}]}", Json.convert(user));
    }

    @Test
    public void testArrayConvert() throws Exception
    {
        ArrayUser user = new ArrayUser("John", "Doe", 18, 2);
        user.addJob(new Job("Developer"));
        user.addJob(new Job("Magician"));
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18,\"jobs\":[{\"name\":\"Developer\"},{\"name\":\"Magician\"}]}", Json.convert(user));
    }

    @Test
    public void testNestedObjectConvert() throws Exception
    {
        NestedUser user = new NestedUser("John", "Doe", 18, new Department("Information technology", new Job("Developer")));
        assertEquals("{\"firstname\":\"John\",\"lastname\":\"Doe\",\"age\":18,\"department\":{\"name\":\"Information technology\",\"job\":{\"name\":\"Developer\"}}}", Json.convert(user));
    }
}