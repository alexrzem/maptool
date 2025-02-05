/*
 * This software Copyright by the RPTools.net development team, and
 * licensed under the Affero GPL Version 3 or, at your option, any later
 * version.
 *
 * MapTool Source Code is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU Affero General Public
 * License * along with this source Code.  If not, please visit
 * <http://www.gnu.org/licenses/> and specifically the Affero license
 * text at <http://www.gnu.org/licenses/agpl.html>.
 */
package net.rptools.maptool.client.ui.tokenpanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;
import net.rptools.lib.image.ImageUtil;
import net.rptools.maptool.client.MapTool;
import net.rptools.maptool.client.swing.SwingUtil;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.util.ImageManager;

public class TokenPanelTreeCellRenderer extends DefaultTreeCellRenderer {

  private BufferedImage image;
  private int row;
  private int rowWidth;

  public Component getTreeCellRendererComponent(
      JTree tree,
      Object value,
      boolean sel,
      boolean expanded,
      boolean leaf,
      int row,
      boolean hasFocus) {

    setBorder(null);

    String text = "";
    this.row = row;

    boolean deemphasize = false;
    if (value instanceof Token) {
      Token token = (Token) value;
      deemphasize = !token.isVisible();

      int height = getPreferredSize().height;
      if (height < 1) {
        height = 15;
      }
      if (image == null || image.getHeight() != height) {
        image = new BufferedImage(height, height, Transparency.TRANSLUCENT);
      } else {
        ImageUtil.clearImage(image);
      }

      // Make a thumbnail of the image
      // TODO: This could be cached somehow, right now it's quick enough though
      BufferedImage tokenImage = ImageManager.getImage(token.getImageAssetId(), this);
      Dimension dim = new Dimension(tokenImage.getWidth(), tokenImage.getHeight());
      SwingUtil.constrainTo(dim, height);

      Graphics2D g = (Graphics2D) image.getGraphics();
      // TODO: Center the image
      g.setComposite(
          AlphaComposite.getInstance(AlphaComposite.SRC_OVER, deemphasize ? 0.5F : 1.0F));
      g.drawImage(tokenImage, 0, 0, dim.width, dim.height, this);
      g.dispose();

      text = token.getName();
      if (MapTool.getPlayer().isGM()
          && token.getGMName() != null
          && token.getGMName().length() > 0) {
        text += " (" + token.getGMName() + ")";
      }
    }
    if (value instanceof TokenPanelTreeModel.View) {
      TokenPanelTreeModel.View view = (TokenPanelTreeModel.View) value;

      text = view.getDisplayName();
    }

    Component c =
        super.getTreeCellRendererComponent(tree, text, sel, expanded, leaf, row, hasFocus);
    c.setFont(c.getFont().deriveFont(deemphasize ? Font.ITALIC : Font.PLAIN));

    Icon icon = getIcon();
    rowWidth =
        (icon != null ? icon.getIconWidth() + 2 : 0)
            + SwingUtilities.computeStringWidth(getFontMetrics(getFont()), text);

    return this;
  }

  @Override
  public Dimension getPreferredSize() {
    int height = row > 0 ? getFontMetrics(getFont()).getHeight() + 4 : 0;
    return new Dimension(super.getPreferredSize().width, height);
  }

  @Override
  public Icon getLeafIcon() {
    return new ImageIcon(image);
  }
}
