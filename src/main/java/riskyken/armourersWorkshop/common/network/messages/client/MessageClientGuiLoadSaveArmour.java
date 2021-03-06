package riskyken.armourersWorkshop.common.network.messages.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import riskyken.armourersWorkshop.common.inventory.ContainerArmourLibrary;
import riskyken.armourersWorkshop.common.tileentities.TileEntitySkinLibrary;

public class MessageClientGuiLoadSaveArmour implements IMessage, IMessageHandler<MessageClientGuiLoadSaveArmour, IMessage> {
    
    private LibraryPacketType packetType;
    private String filename;
    private String filePath;
    private boolean publicList;
    private boolean trackFile;
    
    public MessageClientGuiLoadSaveArmour() {
    }
    
    public MessageClientGuiLoadSaveArmour(String filename, String filePath, LibraryPacketType packetType, boolean publicList, boolean trackFile) {
        this.packetType = packetType;
        this.filename = filename;
        this.filePath = filePath;
        this.publicList = publicList;
        this.trackFile = trackFile;
    }
    
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(this.packetType.ordinal());
        buf.writeBoolean(this.publicList);
        buf.writeBoolean(this.trackFile);
        switch (this.packetType) {
        case CLIENT_SAVE:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        case SERVER_LOAD:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        case SERVER_SAVE:
            ByteBufUtils.writeUTF8String(buf, this.filename);
            ByteBufUtils.writeUTF8String(buf, this.filePath);
            break;
        default:
            break;
        }
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
        this.packetType = LibraryPacketType.values()[buf.readByte()];
        this.publicList = buf.readBoolean();
        this.trackFile = buf.readBoolean();
        switch (this.packetType) {
        case CLIENT_SAVE:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        case SERVER_LOAD:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        case SERVER_SAVE:
            this.filename = ByteBufUtils.readUTF8String(buf);
            this.filePath = ByteBufUtils.readUTF8String(buf);
            break;
        default:
            break;
        }
    }
    
    @Override
    public IMessage onMessage(MessageClientGuiLoadSaveArmour message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) { return null; }
        Container container = player.openContainer;
        
        if (container != null && container instanceof ContainerArmourLibrary) {
            TileEntitySkinLibrary te = ((ContainerArmourLibrary) container).getTileEntity();
            switch (message.packetType) {
            case CLIENT_SAVE:
                te.sendArmourToClient(message.filename, message.filePath, player);
                break;
            case SERVER_LOAD:
                te.loadArmour(message.filename, message.filePath, player, message.trackFile);
                break;
            case SERVER_SAVE:
                te.saveArmour(message.filename, message.filePath, player, message.publicList);
                break;
            default:
                break;
            }
        }
        return null;
    }
    
    public enum LibraryPacketType {
        SERVER_LOAD,
        SERVER_SAVE,
        CLIENT_SAVE;
    }
}
