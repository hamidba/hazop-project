package Application;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import Outils.FormLayout;

public class FenetreProprietes extends JPanel
{

   public FenetreProprietes(Object bean, Component parent)
   {
      this.parent = parent;
      try
      {
         BeanInfo info 
            = Introspector.getBeanInfo(bean.getClass());
         PropertyDescriptor[] descriptors 
            = (PropertyDescriptor[])info.getPropertyDescriptors().clone();      
         Arrays.sort(descriptors, new
            Comparator()
            {
               public int compare(Object o1, Object o2)
               {
                  PropertyDescriptor d1 = (PropertyDescriptor)o1;
                  PropertyDescriptor d2 = (PropertyDescriptor)o2;
                  Integer p1 = (Integer)d1.getValue("priority");
                  Integer p2 = (Integer)d2.getValue("priority");
                  if (p1 == null && p2 == null) return 0;
                  if (p1 == null) return 1;
                  if (p2 == null) return -1;
                  return p1.intValue() - p2.intValue();
               }
            });
         setLayout(new FormLayout());
         for (int i = 0; i < descriptors.length; i++)
         {
            PropertyEditor editor 
               = getEditor(bean, descriptors[i]);
            if (editor != null)
            {
               add(new JLabel(descriptors[i].getName()));
               add(getEditorComponent(editor));
            }
         }
      }
      catch (IntrospectionException exception)
      {
         exception.printStackTrace();
      }
   }

   public PropertyEditor getEditor(final Object bean,
      PropertyDescriptor descriptor)
   {
      try
      {
         Method getter = descriptor.getReadMethod();
         if (getter == null) return null;
         final Method setter = descriptor.getWriteMethod();
         if (setter == null) return null;
         Class type = descriptor.getPropertyType();
         final PropertyEditor editor;
         Class editorClass = descriptor.getPropertyEditorClass();
         if (editorClass == null && editors.containsKey(type))
            editorClass = (Class) editors.get(type);
         if (editorClass != null)            
            editor = (PropertyEditor) editorClass.newInstance();
         else
            editor = PropertyEditorManager.findEditor(type);
         if (editor == null) return null;

         Object value = getter.invoke(bean, new Object[] {});
         editor.setValue(value);
         editor.addPropertyChangeListener(new
            PropertyChangeListener()
            {
               public void propertyChange(PropertyChangeEvent event)
               {
                  try
                  {
                     setter.invoke(bean, 
                        new Object[] { editor.getValue() });
                     fireStateChanged(null);
                  }
                  catch (IllegalAccessException exception)
                  {
                     exception.printStackTrace();
                  }
                  catch (InvocationTargetException exception)
                  {
                     exception.printStackTrace();
                  }
               }
            });
         return editor;
      }
      catch (InstantiationException exception)
      {
         exception.printStackTrace();
         return null;
      }
      catch (IllegalAccessException exception)
      {
         exception.printStackTrace();
         return null;
      }
      catch (InvocationTargetException exception)
      {
         exception.printStackTrace();
         return null;
      }
   }

   public Component getEditorComponent(final PropertyEditor editor)   
   {      
      String[] tags = editor.getTags();
      String text = editor.getAsText();
      if (editor.supportsCustomEditor())
      {
         return editor.getCustomEditor();         
         /*
         

         final JButton button = new JButton();

         if (editor.isPaintable())
         {
            button.setIcon(new 
               Icon()
               {
                  public int getIconWidth() { return WIDTH - 8; }
                  public int getIconHeight() { return HEIGHT - 8; }

                  public void paintIcon(Component c, Graphics g, 
                     int x, int y)
                  {
                     g.translate(x, y);
                     Rectangle r = new Rectangle(0, 0, 
                        getIconWidth(), getIconHeight());
                     Color oldColor = g.getColor();
                     g.setColor(Color.BLACK);
                     editor.paintValue(g, r);
                     g.setColor(oldColor);
                     g.translate(-x, -y);
                  }
               });
         } 
         else 
            button.setText(buttonText(text));
         button.addActionListener(new
            ActionListener()
            {
               public void actionPerformed(ActionEvent event)
               {
                  final Component customEditor = 
                     editor.getCustomEditor();
                   
                  JOptionPane.showMessageDialog(parent,
                     customEditor);
                  
                 if (editor.isPaintable())
                     button.repaint();
                  else 
                     button.setText(buttonText(editor.getAsText()));
               }
            });
         return button;
         */         
      }
      else if (tags != null)
      {
         final JComboBox comboBox = new JComboBox(tags);
         comboBox.setSelectedItem(text);
         comboBox.addItemListener(new
            ItemListener()
            {
               public void itemStateChanged(ItemEvent event)
               {
                  if (event.getStateChange() == ItemEvent.SELECTED)
                     editor.setAsText(
                        (String)comboBox.getSelectedItem());
               }
            });
         return comboBox;
      }
      else 
      {
         final JTextField textField = new JTextField(text, 10);
         textField.getDocument().addDocumentListener(new
            DocumentListener()
            {
               public void insertUpdate(DocumentEvent e) 
               {
                  try
                  {
                     editor.setAsText(textField.getText());
                  }
                  catch (IllegalArgumentException exception)
                  {
                  }
               }
               public void removeUpdate(DocumentEvent e) 
               {
                  try
                  {
                     editor.setAsText(textField.getText());
                  }
                  catch (IllegalArgumentException exception)
                  {
                  }
               }
               public void changedUpdate(DocumentEvent e) 
               {
               }
            });
         return textField;
      }
   }

   private static String buttonText(String text)
   {
      if (text == null || text.equals("")) 
         return " ";
      if (text.length() > MAX_TEXT_LENGTH)
         return text.substring(0, MAX_TEXT_LENGTH) + "...";
      return text;
   }

   public void addChangeListener(ChangeListener listener)
   {
      changeListeners.add(listener);
   }

   private void fireStateChanged(ChangeEvent event)
   {
      for (int i = 0; i < changeListeners.size(); i++)
      {
         ChangeListener listener = (ChangeListener)changeListeners.get(i);
         listener.stateChanged(event);
      }
   }
   
   private ArrayList changeListeners = new ArrayList();
   private Component parent;
   
   private static Map editors;
   
   public static class StringEditor extends PropertyEditorSupport
   {
      public String getAsText() { return (String) getValue(); }
      public void setAsText(String s) { setValue(s); }      
   }
   
   static
   {  
      editors = new HashMap();
      editors.put(String.class, StringEditor.class);
   }
   
   private static final int WIDTH = 100;
   private static final int HEIGHT = 25;
   private static final int MAX_TEXT_LENGTH = 15;
}

