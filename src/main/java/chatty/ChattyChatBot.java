package chatty;

import chatty.exception.ChattyChatBotException;
import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.Task;
import chatty.task.ToDo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static chatty.util.Constants.*;

public class ChattyChatBot {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static String filePath;

    public static void main(String[] args) {
        sendWelcomeMessage();

        List<Task> tasks = new ArrayList<>();

        readDataFromFile(tasks);
        System.out.println(LINE_BREAK);

        String userInput;
        do {
            userInput = SCANNER.nextLine();

            System.out.println(LINE_BREAK);

            // Solution below adapted from: https://stackoverflow
            // .com/questions/5067942/what-is-the-best-way-to-extract-the-first-word-from-a-string-in-java
            String[] array = userInput.split(SPACE_SEPARATOR, 2);
            String action = array[0];

            switch (action) {
            case LIST_STRING:
                listAllTasks(tasks);
                break;
            case DONE_STRING:
                try {
                    markTaskAsDone(tasks, array[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Let me know which task you would like to mark as done?");
                }
                break;
            case TODO_STRING:
                try {
                    addToDoTask(tasks, array[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Sure, let me know what ToDo task you would like to add!");
                }
                break;
            case DEADLINE_STRING:
                try {
                    addDeadlineTask(tasks, array[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Sure, let me know what Deadline task you would like to add!");
                } catch (ChattyChatBotException e) {
                    System.out.println("Please specify the deadline of your task");
                }
                break;
            case EVENT_STRING:
                try {
                    addEventTask(tasks, array[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Sure, let me know what Event task you would like to add!");
                } catch (ChattyChatBotException e) {
                    System.out.println("Please specify the time of your event");
                }
                break;
            case DELETE_STRING:
                try {
                    deleteTask(tasks, array[1]);
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Let me know which task you would like to delete?");
                }
                break;
            case BYE_STRING:
                saveDataToFile(tasks);
                sendByeMessage();
                break;
            default:
                sendDefaultResponse();
            }

            System.out.println(LINE_BREAK);
        } while (!userInput.equals(BYE_STRING));
    }

    private static void listAllTasks(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + DOT_CHARACTER + SPACE_SEPARATOR + task.toString());
        }
    }

    private static void markTaskAsDone(List<Task> tasks, String indexStr) {
        try {
            int taskIdx = Integer.parseInt(indexStr);
            Task task = tasks.get(taskIdx - 1);
            task.markAsDone();
            System.out.println("Congratulations! You've successfully marked the following task as done:");
            System.out.println(task.toString());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid task number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The number you entered does not match any task in your list");
        }
    }

    private static void addToDoTask(List<Task> tasks, String description) {
        ToDo newToDoTask = new ToDo(description.trim());
        tasks.add(newToDoTask);
        confirmAddTask(tasks, newToDoTask);
    }

    private static void addDeadlineTask(List<Task> tasks, String inputStr) throws ChattyChatBotException {
        try {
            String[] array = inputStr.split(BY_STRING);
            Deadline newDeadlineTask = new Deadline(array[0].trim(), array[1].trim());
            tasks.add(newDeadlineTask);
            confirmAddTask(tasks, newDeadlineTask);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChattyChatBotException();
        }
    }

    private static void addEventTask(List<Task> tasks, String inputStr) throws ChattyChatBotException {
        try {
            String[] array = inputStr.split(AT_STRING);
            Event newEventTask = new Event(array[0].trim(), array[1].trim());
            tasks.add(newEventTask);
            confirmAddTask(tasks, newEventTask);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChattyChatBotException();
        }
    }

    private static void confirmAddTask(List<Task> tasks, Task newTask) {
        System.out.println(ADDED_TASK_CONFIRMATION);
        System.out.println(newTask);
        System.out.println(TASK_SUMMARY_FIRST_HALF + tasks.size() + TASK_SUMMARY_SECOND_HALF);
    }

    private static void deleteTask(List<Task> tasks, String indexStr) {
        try {
            int taskIdx = Integer.parseInt(indexStr);
            Task task = tasks.get(taskIdx - 1);
            tasks.remove(taskIdx - 1);
            System.out.println("Successfully deleted the following task:");
            System.out.println(task.toString());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid task number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The number you entered does not match any task in your list");
        }
    }

    private static void sendDefaultResponse() {
        System.out.println("Sorry, I can't help you with that yet");
        System.out.println("I'm " + BOT_NAME);
        System.out.println("How may I help you?");
    }

    private static void sendWelcomeMessage() {
        System.out.println(LINE_BREAK);
        System.out.println("Hello from " + BOT_NAME);
        System.out.println("Glad to be at your service!");
        System.out.println(LINE_BREAK);
    }

    private static void sendByeMessage() {
        System.out.println("Thanks for chatting with " + BOT_NAME);
        System.out.println("See you again soon!");
    }

    private static void readDataFromFile(List<Task> tasks) {
        filePath = DEFAULT_FILE_PATH;
        // Solution below adapted from: https://nus-cs2113-ay1920s2.github.io/website/schedule/week6/topics
        // .html#w6-3-java-file-access
        File file = new File(System.getProperty("user.dir"), filePath);
        System.out.println("Reading tasks from disk...");
        try {
            file.getParentFile().mkdirs();
            if (file.createNewFile()) {
                System.out.println("New output file created");
                System.out.println(file.getAbsolutePath());
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNext()) {
                String taskStr = fileScanner.nextLine();
                Optional<Task> taskOptional = stringToTask(taskStr);
                taskOptional.ifPresent(tasks::add);
            }
        } catch (IOException e) {
            System.out.println("Exception occurred while reading file...");
            System.out.println("Initializing empty tasks list");
            e.printStackTrace();
        }
        listAllTasks(tasks);
    }

    private static void saveDataToFile(List<Task> tasks) {
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            for (Task task : tasks) {
                fileWriter.write(task.getFileString() + NEW_LINE);
            }
            fileWriter.close();
            System.out.println("Your tasks have been successfully saved to disk!");
        } catch (IOException e) {
            System.out.println("Oops! Exception occurred when saving data to file.");
        }
    }

    private static Optional<Task> stringToTask(String taskStr) {
        String[] fields = taskStr.split(FILE_FIELD_SEPARATOR);
        if (fields.length < MINIMUM_FIELD_NUM_FOR_TASK) {
            System.out.println("Invalid line in input file:");
            System.out.println(taskStr);
            return Optional.empty();
        }

        String taskType = fields[0];
        boolean isDone;
        if (fields[1].equals("true")) {
            isDone = true;
        } else if (fields[1].equals("false")) {
            isDone = false;
        } else {
            System.out.println("Wrong format - please use true or false to mark task as done or not done");
            System.out.println(taskStr);
            return Optional.empty();
        }
        String description = fields[2];
        Task task;

        switch (taskType) {
        case "T":
            task = new ToDo(description);
            break;
        case "E":
            if (fields.length < MINIMUM_FIELD_NUM_FOR_EVENT_AND_DEADLINE) {
                System.out.println("Wrong format for Event in input file:");
                System.out.println(taskStr);
                return Optional.empty();
            }
            String eventPeriod = fields[3];
            task = new Event(description, eventPeriod);
            break;
        case "D":
            if (fields.length < MINIMUM_FIELD_NUM_FOR_EVENT_AND_DEADLINE) {
                System.out.println("Wrong format for Deadline in input file:");
                System.out.println(taskStr);
                return Optional.empty();
            }
            String dateTime = fields[3];
            task = new Deadline(description, dateTime);
            break;
        default:
            System.out.println("Task type not specified in input file:");
            System.out.println(taskStr);
            return Optional.empty();
        }

        if (isDone) {
            task.markAsDone();
        }

        return Optional.of(task);
    }
}
