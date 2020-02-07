package chatty;

import chatty.task.Deadline;
import chatty.task.Event;
import chatty.task.Task;
import chatty.task.ToDo;
import chatty.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static chatty.util.Constants.ADDED_TASK_CONFIRMATION;
import static chatty.util.Constants.AT_STRING;
import static chatty.util.Constants.BOT_NAME;
import static chatty.util.Constants.BYE_STRING;
import static chatty.util.Constants.BY_STRING;
import static chatty.util.Constants.DEADLINE_STRING;
import static chatty.util.Constants.DONE_STRING;
import static chatty.util.Constants.DOT_CHARACTER;
import static chatty.util.Constants.EVENT_STRING;
import static chatty.util.Constants.LINE_BREAK;
import static chatty.util.Constants.LIST_STRING;
import static chatty.util.Constants.SPACE_SEPARATOR;
import static chatty.util.Constants.TASK_SUMMARY_FIRST_HALF;
import static chatty.util.Constants.TASK_SUMMARY_SECOND_HALF;
import static chatty.util.Constants.TODO_STRING;

public class ChattyChatBot {

    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        sendWelcomeMessage();

        List<Task> tasks = new ArrayList<>();

        String userInput = "";
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
            case BYE_STRING:
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
            System.out.println(task.toString());
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid task number");
        } catch (IndexOutOfBoundsException e) {
            System.out.println("The number you entered does not match any task in your list");
        }
    }

    private static void addToDoTask(List<Task> tasks, String description) {
        ToDo newToDoTask = new ToDo(description);
        tasks.add(newToDoTask);

        System.out.println(ADDED_TASK_CONFIRMATION);
        System.out.println(newToDoTask);
        System.out.println(TASK_SUMMARY_FIRST_HALF + tasks.size() + TASK_SUMMARY_SECOND_HALF);
    }

    private static void addDeadlineTask(List<Task> tasks, String inputStr) throws ChattyChatBotException {
        try {
            String[] array = inputStr.split(BY_STRING);
            Deadline newDeadlineTask = new Deadline(array[0], array[1]);
            tasks.add(newDeadlineTask);

            System.out.println(ADDED_TASK_CONFIRMATION);
            System.out.println(newDeadlineTask);
            System.out.println(TASK_SUMMARY_FIRST_HALF + tasks.size() + TASK_SUMMARY_SECOND_HALF);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChattyChatBotException();
        }
    }

    private static void addEventTask(List<Task> tasks, String inputStr) throws ChattyChatBotException {
        try {
            String[] array = inputStr.split(AT_STRING);
            Event newEventTask = new Event(array[0], array[1]);
            tasks.add(newEventTask);

            System.out.println(ADDED_TASK_CONFIRMATION);
            System.out.println(newEventTask);
            System.out.println(TASK_SUMMARY_FIRST_HALF + tasks.size() + TASK_SUMMARY_SECOND_HALF);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new ChattyChatBotException();
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
}
