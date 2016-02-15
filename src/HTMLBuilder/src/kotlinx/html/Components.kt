package kotlinx.html

fun StyledElement.roundBorder(width: LinearDimension, color: Color, radius : LinearDimension): Unit {
    attributes["border"] = "$width solid $color"
    attributes["-webkit-border-radius"] = radius
    attributes["-moz-border-radius"] = radius
    attributes["border-radius"] = radius

}
fun StyledElement.gradient(): Unit {
    attributes["background"] = c("#feffff")
    attributes["background"] = "-moz-linear-gradient(top,  #feffff 0%, #e5e1dc 100%); /* FF3.6+ */"
    attributes["background"] = "-webkit-gradient(linear, left top, left bottom, color-stop(0%,#feffff), color-stop(100%,#e5e1dc)); /* Chrome,Safari4+ */"
    attributes["background"] = "-webkit-linear-gradient(top,  #feffff 0%,#e5e1dc 100%); /* Chrome10+,Safari5.1+ */"
    attributes["background"] = "-o-linear-gradient(top,  #feffff 0%,#e5e1dc 100%); /* Opera 11.10+ */"
    attributes["background"] = "-ms-linear-gradient(top,  #feffff 0%,#e5e1dc 100%); /* IE10+ */"
    attributes["background"] = "linear-gradient(to bottom,  #feffff 0%,#e5e1dc 100%); /* W3C */"
    attributes["filter"] = "progid:DXImageTransform.Microsoft.gradient( startColorstr='#feffff', endColorstr='#e5e1dc',GradientType=0 ); /* IE6-9 */"
}

fun StyledElement.shadow(color : String, shift : Int = 1, strength : Int = 3): Unit {
    attributes["-moz-box-shadow"] = "0px ${shift}px ${strength}px $color"
    attributes["-webkit-box-shadow"] = "0px ${shift}px ${strength}px $color"
    attributes["box-shadow"] = "0px ${shift}px ${strength}px $color"
    /* For IE 8 */
    attributes["-ms-filter"] = " \"progid:DXImageTransform.Microsoft.Shadow(Strength=$strength, Direction=180, Color='$color')\";"
    /* For IE 5.5 - 7 */
    attributes["filter"] = "progid:DXImageTransform.Microsoft.Shadow(Strength=$strength, Direction=180, Color='$color');"
}
