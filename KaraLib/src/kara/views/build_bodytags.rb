require 'erb'

# tags that can appear at any place in the body
@body_tags = %w(a b button canvas div em fieldset form h1 h2 h3 h4 h5 img input label ol p select span strong table textarea ul)

# attributes that can be applied to all body tags
@body_attrs = %w(id c style title)

# tags that have child tags that can only be placed inside them
@child_tags = {
	ul: %w(li),
	ol: %w(li),
  select: %w(option optgroup),
  table: %w(tr tbody),
  tr: %w(td th)
}

# special attributes for specific tags
@child_attrs = {
  a: %w(href rel target),
  canvas: %w(width height),
  form: %w(action enctype method),
  img: %w(width height src alt),
  input: %w(accept alt autocomplete autofocus checked disabled height list max maxlength min multiple inputType name pattern placeholder readonly required size src step value width),
  label: %w(forId),
  select: %w(name size multiple disabled),
  option: %w(label value selected disabled),
  option: %w(label disabled),
  textarea: %w(autofocus cols disabled maxlength name placeholder readonly required rows wrap)
}

# a list of attributes that are required
@required_attrs = %w(href src)

# tags that can't have a body
@empty_tags = %w(img input)

# tags that can be created outside of a container (as the root of a view)
@standalone_tags = %w(html div span)

# attributes that have a different name in code to avoid keyword collisions
@attr_aliases = {
  forId: 'for',
  c: 'class',
  inputType: 'type'
}

# gets a list of all tags, including child tags
def all_tags
  (@body_tags + @child_tags.values.flatten).uniq
end

# gets all attributes for the given tags
def attrs_for_tag(tag)
  @body_attrs + child_attrs_for_tag(tag)
end

# gets the child attributes for the given tag, or an empty list if there are none
def child_attrs_for_tag(tag)
  if @child_attrs[tag.to_sym]
    @child_attrs[tag.to_sym]
  else
    []
  end
end

# gets the child tags for the given tag, or an empty list if there are none
def tag_children(tag)
  kids = @child_tags[tag.to_sym]
  if kids
    kids
  else
    []
  end
end

# returns true if the given tag can't have a body
def is_empty?(tag)
  @empty_tags.index(tag.to_s)
end

# gets the alias (html) name for the given attribute
def attr_alias(attr)
  if @attr_aliases.has_key?(attr.to_sym)
    @attr_aliases[attr.to_sym]
  else
    attr
  end
end

# returns true if the given attribute is required
def is_attr_required?(attr)
  @required_attrs.index(attr.to_sym)
end

# generates an argument list string for the given tag
def arg_list_for_tag(tag)
  attrs = attrs_for_tag(tag)
  attrs.map do |attr|
    if is_attr_required?(attr)
      attr.to_s + " : String"
    else
      attr.to_s + " : String = \"\""
    end
  end.join(', ')
end

# generates a function declaration for the given tag
def tag_function(tag)
  tag_cap = tag.upcase
  s = "fun #{tag}(text : String = \"\", #{arg_list_for_tag(tag)}, init : #{tag_cap}.() -> Unit = {}) {\n"
  s += "\t\tval tag = initTag(#{tag_cap}(), #{ is_empty?(tag) ? '{}' : 'init'})\n"
  attrs_for_tag(tag).each do |attr|
    s += "\t\ttag.#{attr} = #{attr}\n"
  end
  s += "\t\tif (tag.children.size == 0) {\n"
  s += "\t\t\ttag.text = text\n"
  s += "\t\t}\n"
  s += "\t}\n"
end