package com.bot.dashboard;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class ErrorDispatch extends JPanel {

    public static final ErrorDispatch panel = new ErrorDispatch();

    public ErrorDispatch() {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public static void dispatch_error(String message) {
        final JLabel error_label = new JLabel("<html><body >" + message + "</body></html>");
        error_label.setForeground(Color.red);

        panel.add(error_label);
        panel.revalidate();
        panel.repaint();

        final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            panel.remove(error_label);
            panel.revalidate();
            panel.repaint();

            scheduler.shutdown();
        }, 5, TimeUnit.SECONDS);
    }

    public interface OperationWithException<T> {
        T run() throws Exception;
    }

    public static <T> T wrap_operation(OperationWithException<T> operation, Consumer<T> consumer) {
        try {
            if(consumer == null) {
                return operation.run();
            }

            consumer.accept(operation.run());
        } catch(Exception exception) {
            dispatch_error(exception.getMessage());
        }

        return null;
    }

}
