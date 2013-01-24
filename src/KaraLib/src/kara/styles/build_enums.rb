
require 'erb'

@attrs = {
  BackgroundAttachment: %w(scroll fixed),
  BackgroundRepeat: %w(repeat repeat-x repeat-y no-repeat),
  BorderStyle: %w(none hidden dotted dashed solid double groove ridge inset outset),
  BoxDirection: %w(normal reverse),
  BoxAlign: %w(start end center baseline),
  BoxLines: %w(single multiple),
  BoxOrient: %w(horizontal vertical inline-axis block-axis),
  BoxPack: %w(start end center),
  BoxSizing: %w(content-box border-box),
  CaptionSide: %w(top bottom),
  Clear: %w(left right both none),
  ColumnFill: %w(balance auto),
  Direction: %w(ltr rtl),
  Display: %w(none block inline inline-block inline-table list-item table table-caption table-cell table-column table-column-group table-footer-group table-header-group table-row table-row-group),
  EmptyCells: %w(hide show),
  Float: %w(left right none),
  FontStyle: %w(normal italic oblique),
  FontVariant: %w(normal small-caps),
  FontWeight: %w(normal bold bolder lighter),
  FontStretch: %w(wider narrower ultra-condensed extra-condensed condensed semi-condensed normal semi-expanded expanded extra-expanded ultra-expanded),
  ListStyleType: %w(circle disc decimal lower-alpha lower-greek lower-latin lower-roman none square upper-alpha upper latin upper-roman),
  ListStylePosition: %w(inside outside),
  Overflow: %w(visible hidden scroll auto no-display no-content),
  PageBreak: %w(auto always avoid left right),
  Position: %w(static absolute fixed relative),
  Resize: %w(none both horizontal vertical),
  TableLayout: %w(auto fixed),
  TextAlign: %w(left right center justify),
  VerticalAlign: %w(top bottom middle),
  Visibility: %w(visible hidden collapse),
  WhiteSpace: %w(normal nowrap pre pre-line pre-wrap),
  WordBreak: %w(normal break-all hyphenate),
  WordWrap: %w(normal break-word)
}

# converts a css attribute name into one that will work in Kotlin
def kotlin_name(attr)
  attr.split('-').map do |a|
    if attr =~ /^#{a}/
      a
    else
      a.capitalize
    end
  end.join
end
