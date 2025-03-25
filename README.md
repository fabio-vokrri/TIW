# Progetto di Tecnologie Informatiche per il Web

Progetto del corso di Tecnologie Informatiche per il Web (a.a. 2024).

Il progetto consiste nell'implementazione di un sito web full stack per la gestione di documenti, che rispetti la seguente specifiche:

L’applicazione supporta registrazione e login mediante una pagina pubblica con opportune form. La registrazione di un utente richiede l’inserimento di username, indirizzo di email e password e controlla la validità sintattica dell’indirizzo di email e l’uguaglianza tra i campi “password” e “ripeti password”. La registrazione controlla l’unicità dello username.  

Una cartella ha un proprietario, un nome e una data di creazione e può contenere altre cartelle e/o documenti. Un documento ha un proprietario, nome, una data di creazione, un sommario e un tipo.

Quando l’utente accede all’applicazione appare una HOME PAGE che contiene un albero delle
proprie cartelle e delle sottocartelle. Nell’HOME PAGE l’utente può selezionare una cartella e accedere a una pagina CONTENUTI che mostra l’elenco delle cartelle e dei documenti di una
cartella.

Ogni documento in elenco ha due link: accedi e sposta. Quando l’utente seleziona il link accedi, appare una pagina DOCUMENTO (nella stessa finestra e tab del browser) che mostra tutti i dati del documento selezionato. Quando l’utente seleziona il link sposta, appare la HOME PAGE con l’albero delle cartelle; in questo caso la pagina mostra il messaggio “Stai spostando il documento X dalla cartella Y. Scegli la cartella di destinazione”, la cartella a cui appartiene il documento da spostare NON è selezionabile e il suo nome è evidenziato (per esempio con un colore diverso). Quando l’utente seleziona la cartella di destinazione, il documento è spostato dalla cartella di origine a quella di destinazione e appare la pagina CONTENUTI che mostra il contenuto aggiornato della cartella di destinazione.

Ogni pagina, tranne la HOME PAGE, contiene un collegamento per tornare alla pagina precedente. L’applicazione consente il logout dell’utente da qualsiasi pagina.

Una pagina GESTIONE CONTENUTI raggiungibile dalla HOME PAGE permette all’utente di creare una cartella di primo livello, una cartella all’interno di una cartella esistente e un documento all’interno di una cartella.

L’applicazione non richiede la gestione dell’upload dei documenti.

Per maggiori informazioni sulla specifica e sulle scelte implementative, consultare la [documentazione](documentation.pdf).
