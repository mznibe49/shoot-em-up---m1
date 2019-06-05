package sample.Others;

import javafx.geometry.Point2D;
import javafx.scene.Node;

public class Utilitaires {

    public static void makeDraggable(Node node) {

        class T {
            double tx, ty, anchorX, anchorY;
        }

        final T t = new T();
        if (node == null) {
            System.err.println("makeDraggable node == null");
        }


        node.setOnMousePressed(event -> {
            /* trouver et memoriser la translation initiale de Node */
            t.tx = node.getTranslateX();
            t.ty = node.getTranslateY();
            /* trouver la position initial de la souris dans les coordonnees
             * du parent de Node
             * et les memoriser dans t.anchor
             */

            Point2D point = node.localToParent(event.getX(), event.getY());
            t.anchorX = point.getX();
            t.anchorY = point.getY();

            /*
            t.anchorX = event.getX();
            t.anchorY = event.getY();
             */
            event.consume();
        });

        node.setOnMouseDragged(event -> {
            /* trouver la position de la souris dans les coordonnees du parent
             * effectuer le deplacement de Node pour suivre les mouvement
             * de la souris
             */

            Point2D point = node.localToParent(event.getX(), event.getY());
            node.setTranslateX(t.tx - t.anchorX + point.getX());
            node.setTranslateY(t.ty - t.anchorY + point.getY());
            /*
            node.setTranslateX(t.initialTranslateX - t.anchorX + event.getX());
            node.setTranslateY(t.initialTranslateY - t.anchorY + event.getY());
             */
            event.consume();
        });

        node.setOnMouseReleased(event -> {
            event.consume();
        });
    }

}