import React from "react";
import Tree from "react-d3-tree";
import "./App.css";
import {useCenteredTree, callAddNode} from "./helpers";
import {useState, useEffect} from "react";

import Popup from 'reactjs-popup';
import 'reactjs-popup/dist/index.css';
import ActionsPopup from "./ActionsPopup";
import AddNodePopup from "./AddNodePopup";
import {getData} from "./helpers";

import {NotificationContainer, NotificationManager} from 'react-notifications';

const containerStyles = {
    width: "100vw",
    height: "100vh"
};

// Here we're using `renderCustomNodeElement` to represent each node
// as an SVG `rect` instead of the default `circle`.
const renderRectSvgNode = ({nodeDatum, setData, setLoading}) => (
    <Popup trigger={
        <g>
            <circle r="10" onClick={() => {
                console.log("Node datum: ", nodeDatum)
            }}/>
            {nodeDatum.attributes !== undefined &&
                <text fill="black" strokeWidth="1" x="20">
                    {nodeDatum.name}
                </text>
            }
            {nodeDatum.attributes?.id &&
                (
                    <text fill="black" x="20" dy="20" strokeWidth="1">
                        ID: {nodeDatum.attributes?.id}
                    </text>
                )}
            {nodeDatum.attributes === undefined &&
                (
                    <text fill="black" x="20" dy="20" strokeWidth="1">
                        Path sum: {nodeDatum.name}
                    </text>
                )}
        </g>
    } modal nested>
        {
            close => (
                <div className='modal'>
                    <div className='content'>
                        <ActionsPopup nodeDatum={nodeDatum} setData={setData}
                                      setLoading={setLoading}/>
                    </div>
                    <div>
                        <button onClick=
                                    {() => close()}>
                            Close modal
                        </button>
                    </div>
                </div>
            )
        }
    </Popup>
);

export const createNotification = (type, message) => {
    console.log(type, typeof (type))
    if (type == 'info') {
        NotificationManager.info(message);
    } else if (type == 'success') {
        NotificationManager.success(message, 'Operation success');
    } else if (type == 'warning') {
        NotificationManager.warning(message, 'Warning');
    } else if (type == 'error') {
        NotificationManager.error(message, "Operation failed");
    } else {
        console.log("default case")
    }
}

export default function App() {
    const [translate, containerRef] = useCenteredTree();

    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        getData(setData, setLoading)
    }, []);

    // console.log("Data2: ", data)
    return (
        <div style={containerStyles} ref={containerRef}>
            {/*<button className='btn btn-success'*/}
            {/*        onClick={createNotification('success', 'test')}>Success*/}
            {/*</button>*/}
            <NotificationContainer/>
            {loading && <div>Data fetching...</div>}
            <Popup trigger={
                <button name={"createButton"}
                    // onClick={()=>createNode()}
                >
                    Create new node
                </button>
            } modal nested>
                {
                    close => (
                        <div>
                            <AddNodePopup setData={setData}
                                          setLoading={setLoading} treeData={data} close={close}/>
                        </div>
                    )
                }

            </Popup>
            {data &&
                <span>
                                        <Tree
                        data={data}
                        translate={translate}
                        renderCustomNodeElement={(rd3tProps) =>
                            renderRectSvgNode({...rd3tProps, setData, setLoading})
                        }
                        orientation="vertical"
                    />

                </span>
            }
        </div>
    );
}

// code based on: https://codesandbox.io/s/react-tree-visualizer-hs3l2