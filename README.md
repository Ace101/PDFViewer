欢迎来到KMPDFKit 1.0.7
----------------------

## 许可证

KMPDFKit是一款商业软件。[联系我们这里的销售团队](https://www.kdanmobile.com/en/pdfsdk)。
通过下载和安装KMPDFKit，您接受本许可证的条款。
一旦您签署了商业许可，请在[联系我们的客户团队](https://www.kdanmobile.com/en/pdfsdk)注册您的应用程序包标识符。

## 开始

1.KMPDFKit自带一个Android AAR存档(KMPDFKit. AAR)。

2.KMPDFKit在Android设备上运行:

    Android 4.0 or newer / API level 15 or higher
    32/64-bit ARM (armeabi-v7a with NEON/ armeabi) or 32-bit Intel x86 CPU


要将KMPDFKit包含到你的应用程序中，你必须使用一个支持Android AAR存档的构建系统，比如Gradle、Maven或类似的东西。不支持Eclipse IDE和Ant构建系统。

#### 集成KMPDFKit

1.复制KMPDFKit AAR文件到您的项目libs目录

2.将libs目录作为存储库添加到构建中。gradle库部分:

    repositories {
        flatDir {
            dirs 'libs'
            }
        }
3.将KMPDFKit作为依赖项包含到您的项目中:

    dependencies {
        ...
        implementation(name: 'kmpdfkit-1.0.7', ext: 'aar')
    }



#### 添加许可密钥
在使用任何KMPDFKit类之前设置许可密钥是非常重要的。

请注意，这是我们的演示项目。默认情况下，将激活所有可用的SDK功能。如果您购买了不同的许可证，请查看是否可以使用相应的SDK功能。

    String license =" jb1/ bdvypymiikkgg2ov9x7ewf19xaobdnot + 2ug75dykdeqcbxrvnsefatp9ohvzc + h8wirpamxutykflrooifsb2zrecrvbpfpqdpk + gh2ziewva7bxphbumm5bb3i1zqs5fqgqnc2z4p0itoga6alwi =";
    RSA_Message =" jKS16XoI0/ gjef3lmrujq0f8vzvo0dytfk1 / crcdgd7f1 +Q8anW8OndP3UFGGk/xuazfDzwQxO+ fcyt9tflpx7tsq6pkjimomoj2tkuhccs0v +hGZbb04V2oE0/SVtLL7yH4W3aJdpz6tSYmsJdt9VgFQo=";
    KMPDFFactory.verifyLicense(this, license, RSA_Message);


#### 显示KMPDFKit ReaderView
1.在KMPDFFactory类中使用以下方法打开PDF文件:

    //magic : Byte stream
    KMPDFFactory kmpdfFactory = KMPDFFactory.open(this, filePath, magic); 

2.Init KMPDFReaderView类:

    KMPDFReaderView readerView = new KMPDFReaderView(context);
    kmpdfFactory.setReaderView(readerView);

3.将readerView添加到ViewGroup布局中:

    layout.addView (readerView);


#### 将页面呈现为位图

您可以使用KMPDFKit将PDF呈现为位图，而不需要在活动中显示它们。为此，使用KMPDFKit和KMPDFDocumentController类调用。

Example:

    KMPDFDocumentController documentController = (KMPDFDocumentController) kmpdfFactory.getController(KMPDFFactory.ControllerType.DOCUMENT);
    //width : Specified bitmap width
    Bitmap bitmap = documentController.covertPDFToBitmap(pageNum, width,  showMode, isDrawAnnot)


#### 如何初始化注释上下文菜单
关于如何初始化Freetext注释上下文菜单，请参考kmpdffact .java文件中的以下方法


    KMPDFMenuItem freetextKMPDFMenuItem = new KMPDFMenuItem();
    freetextKMPDFMenuItem.annotType = KMPDFMenuItem.AnnotType.FREETEXT;
    freetextKMPDFMenuItem.menu_resId = R.menu.freetext;
    freetextKMPDFMenuItem.menuCallbacks.add(0, new MenuItemCallback() {
        @Override
        public boolean excute(View view, KMPDFMenuItem.AnnotType annotType) {
            Toast.makeText(ExampleActivity.this, "delete", Toast.LENGTH_SHORT).show();
            return true;
        }
    });
    kmpdfFactory.setAnnotationContextMenu(freetextKMPDFMenuItem);


#### 如何创建注释
关于如何创建Freetext注释，请参考kmpdffact .java文件中的以下方法

    KMPDFFreetextAnnotationBean freetextAnnotationBean = new KMPDFFreetextAnnotationBean(content, lineColor, textSize, lineAlpha, font_name, bold, italic);
    kmpdfFactory.setAnnotationAttribute(freetextAnnotationBean);
    kmpdfFactory.setAnnotationEditMode(freetextAnnotationBean.type);


#### 如何添加和删除书签
关于如何添加和删除书签，请参考KMPDFDocumentController.java文件中的以下方法

    documentController.addBookmark(title, page);
    documentController.deleteBookmarks(page);

#### 如何保存pdf文档
关于如何保存pdf文档，请参考KMPDFDocumentController.java文件中的以下方法

    if(documentController.hasChanges()){
        documentController.save();
    }


## 支持

For questions or to report issues, open a ticket on our [support platform](https://www.kdanmobile.com/en/pdf-sdk).
Visit [here](https://www.kdanmobile.com/en/pdf-sdk) for the latest news and tips.

谢谢,
KMPDFKit团队
