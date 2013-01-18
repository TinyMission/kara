---
layout: default
title: Kara Stylesheets
isDoc: true
docPage: stylesheets
displayName: Stylesheets
icon: tint
---

## Kara Stylesheets

Like HTML views, CSS stylesheets are also defined with a Kotlin DSL. A stylesheet inherits from kara.styles.Stylesheet, and looks like this:

    class DefaultStyles() : Stylesheet() {
        override fun render() {
            s("body") {
                backgroundColor = c("#f0f0f0")
            }
            s("#main") {
                width = 85.percent
                backgroundColor = c("#fff")
                margin = box(0.px, auto)
                padding = box(1.em)
                border = "1px solid #ccc"
                borderRadius = 5.px
            }

            s("input[type=text], textarea") {
                padding = box(4.px)
                width = 300.px
            }
            s("textarea") {
                height = 80.px
            }

            s("table.fields") {
                s("td") {
                    padding = box(6.px, 3.px)
                }
                s("td.label") {
                    textAlign = TextAlign.right
                }
                s("td.label.top") {
                    verticalAlign = VerticalAlign.top
                }
            }
        }
    }

Each selector is declared with the s() function, and it's attributes are set inside a function literal.
All possible CSS attributes are mapped to properties on the selector, and strongly-typed enums are used for attributes with a predefined set of possible values.
Selector nesting is automatically handled by nesting s() calls and generates the appropriate compound selectors.

Note that special value types exist for dimensional and color attributes. These include:

* Linear dimensions (like width, height, borderRadius, etc.) - created using extension functions on numeric values (i.e. 1.5.em, 3.px, 25.percent)
* Box dimensions (like padding and margin) - created using the box() function, which accepts 1-4 linear dimenions using the same logic as their CSS counterparts
* Colors - created using the c() function and passing a string defining the color

Because the CSS markup is defined using Kotlin itself, the DSL renders CSS preprocessing libraries like SASS and LESS obselete.
Variable and macros can be defined right in code, either inside the style class or as separate functions to share amongst several styles.
