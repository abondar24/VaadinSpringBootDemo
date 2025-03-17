package org.abondar.experimental.articlemanager.ui.author;

import com.vaadin.flow.component.dialog.Dialog;
import lombok.extern.slf4j.Slf4j;
import org.abondar.experimental.articlemanager.model.Author;

import java.util.List;


@Slf4j
public class AuthorConnectionsDialog extends Dialog {


    public AuthorConnectionsDialog(List<Author> authors) {
        setHeaderTitle("Author Connections");

    }
}
