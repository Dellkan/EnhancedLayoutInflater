WORK IN PROGRESS
========
Excuse the mess.

(This project still has a long way to go, and doesn't work as described just yet. Stay tuned.)

Purpose
========
EnhancedLayoutInflater (ELI) main purpose is to make the madness of android layoutinflater
stable and extendable. It is heavily inspired by Calligraphy, and borrows several ideas
and code from it. 

In and by itself through ELI alone, you should notice exactly zero difference in the layouts 
you inflate. However, this will allow you to add in one or many "addons", that each get 
callbacks and functionality that they can use modify the behaviour that occurs when 
any view is inflated. Examples of such behaviours:
 
 - Change or create new shortcuts 
 (such as LinearLayout instead of com.android.widgets.LinearLayout)
 - Add new attributes to both existing and custom/third-party widgets
 - Change existing attributes
 - Initialize `ListView`'s with items and itemlayout directly from the layout file
 - Databinding
 - Set fonts
 - Automatically create xml-layout access for methods that can only be accessed programmatically 
 in vanilla android
 
See the wiki for more details and documentation

Setup
========
TODO

```java
public class MainActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new ELIContextWrapper(newBase, new ELI.Builder()
				.addHook(new CustomAttrHook())
				.addHook(new StyleHook())
				.addHook(new ThemeHook())
		));
	}
}
```

Weirdness, caveats and other peculiar behaviours
========
 - Normally, when messing with custom attributes, you have to define them in the `attrs.xml`.
 This isn't actually a requirement for using ELI, go nuts. 
 However, beware that custom attributes in `styles.xml` must be defined in `attrs.xml`, otherwise,
 your project will refuse to compile (it's an android thing).
 - Found no way of determining namespace-prefixes. This means that you must refer to your namespace
 by the full URI, and not the prefix you're using. Example: `http://schemas.android.com/apk/res-auto`
 instead of `app`. `http://schemas.android.com/apk/res/android` instead of `android`, and so forth.
 For thy convenience, there are utility methods to retrieve attribute ignoring namespace 
 (use with care)
 - Just like the `style` attribute, you may opt to omit the namespace entirely in your custom 
 attributes, however, it should be warned that this is poor XML practice. Include it when you can.
 Having it in there means you get a hint right in the attribute what the source of your attribute is.
 - The namespace URI can be anything, even invalid URI's such as 
 `xmlns:my_namespace="SomethingCompletelyDifferent"`. Again, while possible, this is poor 
 practice and android studio throw a fit about it.

Todo:
========

 - [x] Replicate basic layout inflation
 - [x] Hook into callbacks
 - [x] Setup callback registry
 - [x] Utilities and shortcuts
 - [ ] Testing + weird platforms compatibility
 - [ ] Backwards compatibility
 - [ ] Addon system