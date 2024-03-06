package me.karwsz.rfactor42.objects;

import java.io.File;

public record FileTreeElement(
        File file,
        int depth) {

}
