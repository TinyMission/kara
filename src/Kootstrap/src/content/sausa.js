var sj_globalBindingId = 1;

function bind(node, data) {
    node.each(function () {
        bindNodes(this, data)
    });
    $(document).triggerHandler('binding-complete');

    function bindNodes(template, templateData) {
        var bind = $(template).attr("bind");
        if (bind == undefined) {
            // bind data and children
            bindAttributes(template, templateData);
            $(template).children().each(function () {
                if ($(this).data("expanded-id") == undefined)
                    bindNodes(this, templateData)
            })
        } else {
            // expand nested templates
            var boundValue = bindValue(bind, templateData);
            if (boundValue instanceof Array) {
                // bind array by cloning nodes
                $(template).hide();
                var id = $(template).data("binding-id");
                if (id == undefined) {
                    id = sj_globalBindingId++;
                    $(template).data("binding-id", id);
                }

                var reuse = [];
                var prev = $(template).prev();
                while (prev.data("expanded-id") == id) {
                    reuse.push(prev);
                    prev = prev.prev()
                }

                $.each(boundValue, function (index, templateItemData) {
                    var templateElement = reuse.pop() || $(template).clone();
                    templateElement.removeAttr("bind"); // we don't want to recursively bind items
                    bindNodes(templateElement.get(0), templateItemData);
                    if (templateElement.data("expanded-id") == undefined) {
                        templateElement.data("expanded-id", id);
                        templateElement.insertBefore(template);
                        templateElement.show()
                    }
                });

                // remove remaining nodes, if any
                $.each(reuse, function (index, node) {
                    node.remove()
                });

            } else {
                // bind single value
                bindAttributes(template, boundValue);
                $(template).children().each(function () {
                    bindNodes(this, boundValue)
                })
            }
        }
    }

    function bindAttributes(node, value) {
        // no nested bind, just bind values
        var arr = node.attributes;
        for (var i = 0; i < arr.length; i++) {
            var name = arr[i].nodeName;
            if (name.indexOf("bind-") == 0) {
                var attributeName = name.slice(5, name.length);
                var boundValue = bindValue(arr[i].nodeValue, value);
                if (boundValue != undefined)
                    boundValue = boundValue.toString().replace(/&nbsp;/g, "\u00a0");

                // node.removeAttribute(name);
                if (attributeName == "text") {
                    if ($(node).text() != boundValue)
                        $(node).text(boundValue);
                }
                else if (attributeName == "html") {
                    if ($(node).html() != boundValue)
                        $(node).html(boundValue)
                }
                else {
                    if ($(node).attr(attributeName) != boundValue)
                        $(node).attr(attributeName, boundValue)
                }
            }
        }
    }

    function bindValue(binding, value) {
        var parameters = binding.split(":");
        if (parameters.length > 1) {
            var command = parameters[0];
            switch (command) {
                case "if":
                    return command_if(parameters, value);
            }
            return undefined
        }

        return bindProperties(binding, value)
    }

    function command_if(parameters, value) {
        if (parameters.length < 3)
            return undefined;
        var condition = bindProperties(parameters[1], value);
        if ((typeof condition == "boolean" && condition)
            || (typeof condition == "number" && parseInt(condition) != 0)
            || (typeof condition == "string" && condition.toUpperCase() == "TRUE"))
            return bindProperties(parameters[2], value);
        if (parameters.length >= 4)
            return bindProperties(parameters[3], value);
        return undefined
    }

    function bindProperties(binding, value) {
        if (binding[0] != "@") // no properties
            return binding;

        var properties = binding.slice(1).split(".");
        var boundValue = value;
        for (var i = 0; i < properties.length; i++) {
            boundValue = boundValue[properties[i]];
            if (boundValue == undefined) return undefined;
        }
        return boundValue;
    }
}

function executeData(node, data) {
    var use = node.attr("data-use");
    switch (use) {
        case "bind":
            var bindTo = node.attr("data-bind");
            if (bindTo != undefined)
                bind($(bindTo), data);
            else
                bind(node, data);
            break;
    }
}

function fetch(node) {
    var retry = 0;
    $.ajax({
        url: node.attr("data-url"),
        context: node,
        dataType: node.attr("data-type") || "json"
    })
        .done(function (data) {
            executeData($(this), data);
        })
        .fail(function () {
            retry = retry + 1;
            setTimeout(function () {
                fetch($(this))
            }, 10000 * retry);
        });
}

$(function () {
    $('[data-url]').each(function () {
        var node = $(this);
        fetch(node);
        var interval = node.attr("data-interval");
        if (interval != undefined) {
            setTimeout(function () {
                fetch(node)
            }, parseInt(interval) * 1000);
        }
    })
});

$(function () {
    function appendAttributes(data, node, attributes) {
        var properties = attributes.split(",");
        for (var i = 0; i < properties.length; i++) {
            var name = properties[i].trim();
            var value = node.attr(name);
            if (value != undefined) {
                data[name] = value;
            }
        }
    }

    function roundTripData(node, data, done) {
        $.ajax({
            url: node.attr('send-url'),
            cache: false,
            context: node,
            type: node.attr('send-method') || "GET",
            dataType: "json",
            data: data
        }).done(function (data) {
                executeData(node, data);
                var fetchSelector = node.attr('send-fetch');
                if (fetchSelector != undefined) {
                    fetch($(fetchSelector))
                }
                if (done != undefined)
                    done(node, data)
            });
    }

    // submit json on link
    $(document).on('click', 'a[send-url]', function (e) {
        var data = { };
        appendAttributes(data, $(this), $(this).attr('send-values'));
        roundTripData($(this), data);
        e.preventDefault()
    });

    $(document).on('change', 'input[send-url]', function (e) {
        var data = { "value": $(this).val() };
        appendAttributes(data, $(this), $(this).attr('send-values'));
        roundTripData($(this), data);
        e.preventDefault()
    });

    // submit json on form.submit
    $(document).on('submit', 'form[send-url]', function (e) {
        var data = $(this).serialize();
        roundTripData($(this), data, function (node, data) {
                node.each(function () {
                    this.reset()
                })
            }
        );
        e.preventDefault()
    })
});
